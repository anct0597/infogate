package vn.infogate.ispider.common.properties;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Properties;

@Slf4j
public class ConfigProperties {

    public static final String CONFIG_PATH = "./config";
    private final Properties properties;

    private ConfigProperties() {
        this.properties = new Properties();
        this.readProperties();
    }

    private void readProperties() {
        try (var is = new BufferedInputStream(new FileInputStream(CONFIG_PATH))) {
            properties.load(is);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    public static ConfigProperties getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final ConfigProperties INSTANCE = new ConfigProperties();
    }
}
