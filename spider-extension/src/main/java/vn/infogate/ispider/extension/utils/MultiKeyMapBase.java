package vn.infogate.ispider.extension.utils;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("rawtypes")
public abstract class MultiKeyMapBase {

    protected static final Class<? extends Map> DEFAULT_CLAZZ = HashMap.class;

    private Class<? extends Map> protoMapClass = DEFAULT_CLAZZ;

    public MultiKeyMapBase() {
    }

    public MultiKeyMapBase(Class<? extends Map> protoMapClass) {
        this.protoMapClass = protoMapClass;
    }

    @SuppressWarnings("unchecked")
    protected <K, V2> Map<K, V2> newMap() {
        try {
            return (Map<K, V2>) protoMapClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("wrong proto type map " + protoMapClass);
        }
    }
}