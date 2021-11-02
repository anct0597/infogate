package vn.infogate.ispider.storage.repo;

import vn.infogate.ispider.storage.model.document.PropertyInfoDoc;
import vn.infogate.ispider.storage.solr.DelaySolrRepositoryImpl;

public class PropertyInfoRepo extends DelaySolrRepositoryImpl<PropertyInfoDoc> {

    private PropertyInfoRepo() {
        super("property-info", PropertyInfoDoc.class);
    }

    public static PropertyInfoRepo getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final PropertyInfoRepo INSTANCE = new PropertyInfoRepo();
    }

    @Override
    protected String getId(PropertyInfoDoc bean) {
        return bean.getId();
    }
}
