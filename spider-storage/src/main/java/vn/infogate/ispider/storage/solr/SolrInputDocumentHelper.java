package vn.infogate.ispider.storage.solr;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.SolrInputDocument;
import vn.infogate.ispider.storage.solr.model.UDoc;
import vn.infogate.ispider.storage.solr.model.UField;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class SolrInputDocumentHelper {

    private SolrInputDocumentHelper() {
    }

    public static SolrInputDocument buildUpdateDoc(UDoc uDoc) {
        return buildUpdateDoc(uDoc.getId(), uDoc.getRoot(), uDoc.getUpdateFields());
    }

    public static SolrInputDocument buildUpdateDoc(String id, Set<UField> updateFields) {
        return buildUpdateDoc(id, null, updateFields);
    }

    /**
     * Build solr input document.
     *
     * @param id           id.
     * @param root         root.
     * @param updateFields field has change value.
     */
    public static SolrInputDocument buildUpdateDoc(String id, String root, Set<UField> updateFields) {
        var inputDoc = new SolrInputDocument();
        inputDoc.addField("id", id);
        if (StringUtils.isNotEmpty(root)) {
            inputDoc.addField("_root_", root);
        }
        for (var uField : updateFields) {
            var fieldMap = new HashMap<String, Object>(1);
            fieldMap.put(uField.getAction().getName(), uField.getValue());
            inputDoc.addField(uField.getName(), fieldMap);
        }
        return inputDoc;
    }

    /**
     * Build child doc.
     *
     * @param id     id.
     * @param fields all fields.
     */
    public static SolrInputDocument buildDoc(String id, Map<String, Object> fields) {
        var inputDoc = new SolrInputDocument();
        inputDoc.addField("id", id);
        for (var uField : fields.entrySet()) {
            inputDoc.addField(uField.getKey(), uField.getValue());
        }
        return inputDoc;
    }
}
