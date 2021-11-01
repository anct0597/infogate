package vn.infogate.ispider.storage.solr;

import org.apache.solr.client.solrj.beans.BindingException;
import org.apache.solr.client.solrj.beans.Field;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import vn.infogate.ispider.storage.model.SField;

import java.lang.reflect.AccessibleObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings({"rawtypes", "unchecked"})
public class DocumentBeanMapper {

    private final Map<Class, List<SField>> docFieldsCache;

    public DocumentBeanMapper() {
        this.docFieldsCache = new ConcurrentHashMap<>();
    }

    public <T> T toBean(Class<T> clazz, SolrDocument document) {
        try {
            var fields = getFields(clazz);
            T bean = clazz.getConstructor().newInstance();
            for (var field : fields) {
                var fieldValue = document.getFieldValue(field.getName());
                if (fieldValue == null) continue;
                if (field.isChild()) {
                    if (Collection.class.isAssignableFrom(field.getType())) {
                        var docs = getChildrenDocument(fieldValue);
                        var result = new ArrayList<>();
                        for (var doc : docs) {
                            result.add(toBean(field.getChildType(), doc));
                        }
                        field.inject(bean, result);
                    }
                } else {
                    field.inject(bean, fieldValue);
                }
            }
            return bean;
        } catch (Exception e) {
            throw new BindingException("Could not instantiate object of " + clazz, e);
        }
    }

    public List<SolrDocument> getChildrenDocument(Object fieldValue) {
        if (checkMultipleNestedDocument(fieldValue)) {
            return (List<SolrDocument>) fieldValue;
        }

        var collections = new ArrayList<SolrDocument>(1);
        collections.add((SolrDocument) fieldValue);
        return collections;
    }

    private boolean checkMultipleNestedDocument(Object fieldValue) {
        try {
            var documents = (List<Object>) fieldValue;
            return documents.get(0) instanceof SolrDocument;
        } catch (Exception e) {
            return false;
        }
    }

    public SolrInputDocument toSolrInputDocument(Object obj) {
        var fields = getFields(obj.getClass());
        var doc = new SolrInputDocument();
        for (var field : fields) {
            if (field.isChild()) {
                addChild(obj, field, doc);
            } else {
                doc.setField(field.getName(), field.get(obj));
            }
        }
        return doc;
    }

    private void addChild(Object obj, SField field, SolrInputDocument doc) {
        var value = field.get(obj);
        if (value == null) return;
        if (value instanceof Collection) {
            var children = getChildren((Collection) value);
            doc.addField(field.getName(), children);
        } else if (value.getClass().isArray()) {
            Object[] objs = (Object[]) value;
            var children = getChildren(Arrays.asList(objs));
            doc.addField(field.getName(), children);
        } else {
            doc.addField(field.getName(), toSolrInputDocument(value));
        }
    }

    private List<SolrInputDocument> getChildren(Collection list) {
        var children = new ArrayList<SolrInputDocument>(list.size());
        for (var c : list) {
            var child = toSolrInputDocument(c);
            children.add(child);
        }
        return children;
    }

    private List<SField> getFields(Class<?> clazz) {
        var fields = docFieldsCache.get(clazz);
        if (fields == null) {
            synchronized (docFieldsCache) {
                fields = resolveFields(clazz);
                docFieldsCache.put(clazz, fields);
            }
        }
        return fields;
    }

    private List<SField> resolveFields(Class<?> clazz) {
        var fields = new ArrayList<SField>();
        Class<?> superClazz = clazz;
        var members = new ArrayList<AccessibleObject>();

        while (superClazz != null && superClazz != Object.class) {
            members.addAll(Arrays.asList(superClazz.getDeclaredFields()));
            members.addAll(Arrays.asList(superClazz.getDeclaredMethods()));
            superClazz = superClazz.getSuperclass();
        }
        for (AccessibleObject member : members) {
            if (member.isAnnotationPresent(Field.class)) {
                fields.add(new SField(member));
            }
        }
        return fields;
    }
}
