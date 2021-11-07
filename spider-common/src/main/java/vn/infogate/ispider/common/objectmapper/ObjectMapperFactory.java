package vn.infogate.ispider.common.objectmapper;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author anct
 */
public class ObjectMapperFactory {

    public static ObjectMapper getInstance() {
        return SingletonHelper.INSTANCE;
    }

    private static class SingletonHelper {
        private static final ObjectMapper INSTANCE = new ObjectMapper();
    }
}
