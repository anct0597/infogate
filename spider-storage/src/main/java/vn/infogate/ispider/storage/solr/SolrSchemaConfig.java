package vn.infogate.ispider.storage.solr;

import lombok.Getter;
import vn.infogate.ispider.common.properties.ConfigProperties;

@Getter
public class SolrSchemaConfig {

    private final String remote;
    private final String username;
    private final String password;

    private SolrSchemaConfig(String remote, String username, String password) {
        this.remote = remote;
        this.username = username;
        this.password = password;
    }

    public static SolrSchemaConfig create(String schema) {
        var properties = ConfigProperties.getInstance();
        var remote = properties.get(String.format("solr.%s.remote", schema));
        var username = properties.get(String.format("solr.%s.username", schema));
        var password = properties.get(String.format("solr.%s.password", schema));
        return new SolrSchemaConfig(remote, username, password);
    }
}
