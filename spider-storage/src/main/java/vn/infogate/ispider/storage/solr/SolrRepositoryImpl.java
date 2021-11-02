package vn.infogate.ispider.storage.solr;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import vn.infogate.ispider.storage.solr.model.Page;
import vn.infogate.ispider.storage.solr.model.UField;
import vn.infogate.ispider.storage.solr.exception.RepositoryException;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@Slf4j
public abstract class SolrRepositoryImpl<E> {

    @Getter
    protected SolrClient solrClient;
    protected DocumentBeanMapper mapper;
    protected Class<E> beanClazz;

    protected SolrRepositoryImpl(String schema, Class<E> beanClazz) {
        this.mapper = new DocumentBeanMapper();
        this.buildSolrClient(schema);
        this.beanClazz = beanClazz;
    }

    protected void buildSolrClient(String schema) {
        var config = SolrSchemaConfig.create(schema);
        this.solrClient = SolrClientCreator.create(config.getRemote(),
                config.getUsername(), config.getPassword(), 60000);
    }

    /**
     * Save and commit.
     *
     * @param bean bean data.
     * @throws RepositoryException when error occurred.
     */
    public void save(E bean) throws RepositoryException {
        try {
            var doc = this.mapper.toSolrInputDocument(bean);
            if (doc != null) {
                this.solrClient.add(doc);
                this.solrClient.commit(false, false);
            }
        } catch (IOException | SolrServerException ex) {
            throw new RepositoryException(ex);
        }
    }

    /**
     * Save and commit within max ms.
     *
     * @param bean bean data.
     * @throws RepositoryException when error occurred.
     */
    public void save(E bean, int commitWithinMs) throws RepositoryException {
        try {
            var doc = this.mapper.toSolrInputDocument(bean);
            if (doc != null) {
                this.solrClient.add(doc, commitWithinMs);
            }
        } catch (IOException | SolrServerException ex) {
            throw new RepositoryException(ex);
        }
    }

    /**
     * Update data, then make a hard commit.
     *
     * @param inputDoc input doc.
     * @throws RepositoryException when error occurred.
     */
    public void update(SolrInputDocument inputDoc) throws RepositoryException {
        try {
            this.solrClient.add(inputDoc);
            this.solrClient.commit(false, false);
        } catch (IOException | SolrServerException ex) {
            throw new RepositoryException(ex);
        }
    }

    /**
     * Update data, then commit within max ms.
     *
     * @param inputDoc input doc.
     * @throws RepositoryException when error occurred.
     */
    public void update(SolrInputDocument inputDoc, int commitWithinMs) throws RepositoryException {
        try {
            this.solrClient.add(inputDoc, commitWithinMs);
        } catch (IOException | SolrServerException ex) {
            throw new RepositoryException(ex);
        }
    }

    /**
     * Update data, then commit.
     *
     * @param id      unique id of doc.
     * @param uFields list changed fields.
     * @throws RepositoryException when error occurred.
     */
    public void update(String id, Set<UField> uFields) throws RepositoryException {
        update(SolrInputDocumentHelper.buildUpdateDoc(id, uFields));
    }

    /**
     * Update data, then commit.
     *
     * @param id      unique id of doc.
     * @param root    unique id.
     * @param uFields list changed fields.
     * @throws RepositoryException when error occurred.
     */
    public void updateChild(String id, String root, Set<UField> uFields) throws RepositoryException {
        update(SolrInputDocumentHelper.buildUpdateDoc(id, root, uFields));
    }

    /**
     * Delete without commit.
     *
     * @param id id.
     * @throws RepositoryException ex.
     */
    public void delete(String id) throws RepositoryException {
        try {
            this.solrClient.deleteById(id);
            this.solrClient.commit(false, false);
        } catch (SolrServerException | IOException ex) {
            log.error(ex.getMessage(), ex);
            throw new RepositoryException(ex);
        }
    }

    /**
     * Delete and commit in max ms.
     *
     * @param id id.
     * @throws RepositoryException ex.
     */
    public void delete(String id, int commitWithinMs) throws RepositoryException {
        try {
            this.solrClient.deleteById(id, commitWithinMs);
        } catch (SolrServerException | IOException ex) {
            log.error(ex.getMessage(), ex);
            throw new RepositoryException(ex);
        }
    }

    /**
     * Search with solr query and paging.
     * Resp as native.
     *
     * @param solrQuery built solr query.
     */
    public QueryResponse query(SolrQuery solrQuery) throws RepositoryException {
        try {
            return this.solrClient.query(solrQuery);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new RepositoryException(ex);
        }
    }

    /**
     * Get bean by query and fields.
     */
    public E getByQuery(String query, String... fields) {
        var solrQuery = new SolrQuery();
        solrQuery.setQuery(query);
        solrQuery.setFields(fields);
        solrQuery = solrQuery.setStart(0);

        try {
            var rsp = this.solrClient.query(solrQuery);
            var iterator = rsp.getResults().iterator();
            return iterator.hasNext() ? this.mapper.toBean(beanClazz, iterator.next()) : null;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    /**
     * Check exist document by id field.
     */
    public boolean exist(String id) {
        var solrQuery = new SolrQuery();
        solrQuery.setQuery(String.format("id: %s", id));
        solrQuery.setFields("id");
        solrQuery = solrQuery.setStart(0);

        try {
            var rsp = this.solrClient.query(solrQuery);
            return !rsp.getResults().isEmpty();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return false;
    }


    /**
     * Search by native query and paging.
     *
     * @param query       fully solr query.
     * @param currentPage current page.
     * @param pageSize    page size.
     */
    public Page<E> search(String query, int currentPage, int pageSize) {
        var solrQuery = new SolrQuery();
        log.info("Query string: {}", query);
        solrQuery.setQuery(query);
        return this.search(solrQuery, currentPage, pageSize);
    }

    /**
     * Search with solr query and paging.
     * Resp as page.
     *
     * @param solrQuery   built solr query.
     * @param currentPage current page.
     * @param pageSize    pageSize.
     */
    public Page<E> search(SolrQuery solrQuery, int currentPage, int pageSize) {
        Page<E> page = new Page<>(currentPage, 0);

        try {
            solrQuery.setStart((currentPage - 1) * pageSize);
            solrQuery.setRows(pageSize);
            var rsp = this.solrClient.query(solrQuery);
            long numberOfResult = rsp.getResults().getNumFound();
            page.computePagesAvailable(numberOfResult, pageSize);
            page.setTotalItems(numberOfResult);
            page.setTime(rsp.getElapsedTime());

            for (SolrDocument doc : rsp.getResults()) {
                tryConvert(doc).ifPresent(bean -> page.getPageItems().add(bean));
            }
        } catch (Exception ex) {
            log.error(solrQuery.getQuery() + " - " + ex.getMessage(), ex);
        }

        return page;
    }

    /**
     * Search with solr query and paging.
     * Resp as native.
     *
     * @param solrQuery   built solr query.
     * @param currentPage current page.
     * @param pageSize    pageSize.
     */
    public QueryResponse query(SolrQuery solrQuery, int currentPage, int pageSize) throws RepositoryException {
        try {
            solrQuery.setStart((currentPage - 1) * pageSize);
            solrQuery.setRows(pageSize);
            return this.solrClient.query(solrQuery);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new RepositoryException(ex);
        }
    }

    /**
     * Search with solr query and paging.
     * Resp as native document.
     *
     * @param solrQuery   built solr query.
     * @param currentPage current page.
     * @param pageSize    pageSize.
     */
    public Page<SolrDocument> searchNative(SolrQuery solrQuery, int currentPage, int pageSize) {
        Page<SolrDocument> page = new Page<>(currentPage, 0);

        try {
            var rsp = this.query(solrQuery, currentPage, pageSize);
            long numberOfResult = rsp.getResults().getNumFound();
            page.computePagesAvailable(numberOfResult, pageSize);
            page.setTotalItems(numberOfResult);
            page.setTime(rsp.getElapsedTime());
            for (SolrDocument doc : rsp.getResults()) {
                page.getPageItems().add(doc);
            }
        } catch (Exception ex) {
            log.error(solrQuery.getQuery() + " - " + ex.getMessage(), ex);
        }

        return page;
    }

    /**
     * Get bean by single field.
     */
    public E get(String field, String value) {
        return this.getNestedByQuery(field + ":" + value);
    }

    /**
     * Get bean by id.
     */
    public E getById(String id) {
        return this.get("id", id);
    }

    /**
     * Get bean and nest documents.
     *
     * @param query solr query.
     */
    public E getNestedByQuery(String query) {
        return getByQuery(query, "*", "[child]");
    }

    /**
     * Try to convert from document to bean.
     */
    private Optional<E> tryConvert(SolrDocument doc) {
        try {
            return Optional.ofNullable(this.mapper.toBean(beanClazz, doc));
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return Optional.empty();
    }

    public void destroy() throws Exception {
        this.solrClient.close();
    }
}
