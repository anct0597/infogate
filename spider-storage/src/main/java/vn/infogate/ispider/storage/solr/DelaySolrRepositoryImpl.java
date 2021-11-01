package vn.infogate.ispider.storage.solr;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import vn.infogate.ispider.common.utils.UUIdUtils;
import vn.infogate.ispider.common.utils.Utils;
import vn.infogate.ispider.storage.model.AtomicUpdate;
import vn.infogate.ispider.storage.model.UDoc;
import vn.infogate.ispider.storage.model.UField;
import vn.infogate.ispider.storage.solr.exception.MethodNotAvailableException;
import vn.infogate.ispider.storage.solr.exception.RepositoryException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.*;

@Slf4j
public abstract class DelaySolrRepositoryImpl<E> extends SolrRepositoryImpl<E> {

    protected Map<String, E> addQueue;
    protected Map<String, UDoc> updateQueue;
    protected LinkedBlockingQueue<String> deleteQueue;
    protected ScheduledExecutorService scheduleService;
    private static final String ERR_MSG = "Method not available, please check again";

    protected DelaySolrRepositoryImpl(String schema, Class<E> beanClazz) {
        super(schema, beanClazz);
        this.addQueue = new ConcurrentHashMap<>();
        this.updateQueue = new ConcurrentHashMap<>();
        this.deleteQueue = new LinkedBlockingQueue<>();
        this.scheduleService = Executors.newSingleThreadScheduledExecutor();
        this.scheduleService.scheduleWithFixedDelay(this::persistQueue, 1, 1, TimeUnit.SECONDS);
    }

    private void persistQueue() {
        this.deleteDocuments();
        this.addDocuments();
        this.updateDocuments();
    }

    /**
     * Add documents to solr.
     * Force commit when add more than 15 docs.
     */
    protected synchronized void addDocuments() {
        if (this.addQueue.isEmpty()) return;
        var counter = 0;
        var iterator = this.addQueue.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, E> entry = iterator.next();
            this.addBean(entry.getValue());
            ++counter;
            iterator.remove();
            if (counter >= 15) {
                try {
                    this.solrClient.commit(false, false);
                } catch (SolrServerException | IOException ex) {
                    log.error(ex.getMessage(), ex);
                } finally {
                    counter = 0;
                }
            }
        }
        if (counter > 0) commit();
    }

    /**
     * Update doc to Solr in queue.
     * if update success or fail count > 2 then remove solr doc from queue.
     * Force commit when delete more than 15 docs.
     */
    protected synchronized void updateDocuments() {
        if (this.updateQueue.isEmpty()) return;
        var counter = 0;
        var iterator = this.updateQueue.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, UDoc> entry = iterator.next();
            var uDoc = entry.getValue();
            var updateSuccess = this.updateDoc(uDoc);
            if (updateSuccess || uDoc.getUpdateFailCount() > 2) {
                ++counter;
                iterator.remove();
            }
            if (counter >= 15) {
                try {
                    this.solrClient.commit(false, false);
                } catch (SolrServerException | IOException ex) {
                    log.error(ex.getMessage(), ex);
                } finally {
                    counter = 0;
                }
            }
        }
        if (counter > 0) commit();
    }

    /**
     * Delete documents from solr.
     * Force commit when delete more than 15 docs.
     */
    protected synchronized void deleteDocuments() {
        if (this.deleteQueue.isEmpty()) return;
        var counter = 0;
        var iterator = this.deleteQueue.iterator();
        while (iterator.hasNext()) {
            this.deleteDocByQuery(iterator.next());
            ++counter;
            if (counter >= 15) {
                try {
                    this.solrClient.commit(false, false);
                } catch (SolrServerException | IOException ex) {
                    log.error(ex.getMessage(), ex);
                } finally {
                    counter = 0;
                }
            }
            iterator.remove();
        }
        if (counter > 0) commit();
    }

    /**
     * Add a new document without commit.
     *
     * @param bean bean data.
     */
    private void addBean(E bean) {
        try {
            var doc = mapper.toSolrInputDocument(bean);
            if (doc != null) {
                this.solrClient.add(doc);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Update a document without commit.
     * return true if update success or has unchecked exception.
     * if return true, then remove document from queue.
     *
     * @param updateDoc change doc.
     */
    private boolean updateDoc(UDoc updateDoc) {
        try {
            var doc = SolrInputDocumentHelper.buildUpdateDoc(updateDoc);
            this.solrClient.add(doc);
        } catch (Exception e) {
            if (e.getMessage().contains("Did not find child ID")
                    || e.getMessage().contains("Document not found for update")) {
                updateDoc.increaseFailCount();
                log.warn(e.getMessage());
                return false;
            }
            log.error(e.getMessage(), e);
        }
        return true;
    }

    /**
     * Delete a single doc without commit.
     *
     * @param id id.
     */
    private void deleteDocByQuery(String id) {
        try {
            var query = String.format("id: %s", id);
            this.solrClient.deleteByQuery(query);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void commit() {
        try {
            this.solrClient.commit(false, false);
        } catch (SolrServerException | IOException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void save(E bean) {
        var id = getId(bean);
        if (StringUtils.isEmpty(id)) {
            id = UUIdUtils.generateSimpleUUID();
        }
        this.addQueue.put(id, bean);
    }

    @Override
    public void delete(String id) {
        if (StringUtils.isNotEmpty(id)) {
            this.deleteQueue.add(id);
        }
    }

    public void deleteChild(String parentId, String id) {
        if (StringUtils.isNotEmpty(parentId)) {
            //remove action to avoid delete before update
            if (updateQueue.containsKey(parentId)) {
                removeDocumentInQueue(parentId, id);
            }
            this.deleteQueue.add(id);
        }
    }

    /**
     * remove child document in updateQueue.
     *
     * @param parentId parentId
     * @param id       documentId
     */
    private void removeDocumentInQueue(String parentId, String id) {
        var currentFields = this.updateQueue.get(parentId).getUpdateFields();
        currentFields.removeIf(uField -> {
            if (uField.getValue() instanceof SolrInputDocument) {
                var document = (SolrInputDocument) uField.getValue();
                var documentId = document.get("id");
                return Objects.nonNull(documentId) && id.equals(documentId.getValue());
            }
            return false;
        });
    }

    @Override
    public void update(String id, Set<UField> uFields) throws RepositoryException {
        if (StringUtils.isNotEmpty(id)) {
            if (updateQueue.containsKey(id)) {
                var currentFields = this.updateQueue.get(id).getUpdateFields();
                updateFields(uFields, currentFields);
            } else {
                this.updateQueue.put(id, UDoc.createUpdateDoc(id, uFields));
            }
        }
    }

    @Override
    public void updateChild(String id, String root, Set<UField> uFields) {
        if (StringUtils.isNotEmpty(id)) {
            if (updateQueue.containsKey(id)) {
                var currentFields = this.updateQueue.get(id).getUpdateFields();
                updateFields(uFields, currentFields);
            } else {
                this.updateQueue.put(id, UDoc.createUpdateChildDoc(id, root, uFields));
            }
        }
    }

    /**
     * If update field has ADD action and solr field is multiValue
     * => combine current values & update values.
     *
     * @param uFields       solr fields need update.
     * @param currentFields current fields in queue.
     */
    private void updateFields(Set<UField> uFields, Set<UField> currentFields) {
        for (var uField : uFields) {
            var found = false;
            for (var cField : currentFields) {
                if (uField.getName().equals(cField.getName())) {
                    if (uField.getAction() == AtomicUpdate.ADD) {
                        var newField = makeNewUpdateField(uField, cField);
                        currentFields.remove(cField);
                        currentFields.add(newField);
                    } else {
                        currentFields.remove(cField);
                        currentFields.add(uField);
                    }
                    found = true;
                    break;
                }
            }
            if (!found) currentFields.add(uField);
        }
    }

    /**
     * Make a new solr field update.
     *
     * @param uField update field.
     * @param cField current field.
     */
    private UField makeNewUpdateField(UField uField, UField cField) {
        var newValue = new ArrayList<>();
        Utils.mergeValue(newValue, uField.getValue());
        Utils.mergeValue(newValue, cField.getValue());
        return UField.add(uField.getName(), newValue);
    }

    @Override
    public void update(SolrInputDocument inputDoc, int commitWithinMs) {
        throw new MethodNotAvailableException(ERR_MSG);
    }

    @Override
    public void delete(String id, int commitWithinMs) {
        throw new MethodNotAvailableException(ERR_MSG);
    }

    @Override
    public void save(E bean, int commitWithinMs) {
        throw new MethodNotAvailableException(ERR_MSG);
    }

    @Override
    public void update(SolrInputDocument inputDoc) {
        throw new MethodNotAvailableException(ERR_MSG);
    }

    protected abstract String getId(E bean);
}
