package vn.infogate.ispider.common.utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class Utils {

    private Utils() {
    }

    public static boolean isNullOrEmpty(Object obj) {
        if (obj instanceof Collection) {
            return ((Collection<?>) obj).isEmpty();
        }
        if (obj instanceof String) {
            return ((String) obj).isEmpty() || ((String) obj).isBlank();
        }
        if (obj instanceof Map) {
            return ((Map<?, ?>) obj).isEmpty();
        }
        return obj == null;
    }

    public static void mergeValue(Collection<Object> c1, Object obj) {
        if (obj instanceof Collection) {
            c1.addAll((Collection<?>) obj);
        } else {
            c1.add(obj);
        }
    }

    public static String getAsStr(Map<String, Object> map, String key) {
        var value = map.get(key);
        return value == null ? null : String.valueOf(value);
    }

    public static Integer getAsInt(Map<String, Object> map, String key) {
        var value = map.get(key);
        return value == null ? null : (Integer) value;
    }

    public static Long getAsLong(Map<String, Object> map, String key) {
        var value = map.get(key);
        return value == null ? null : (Long) value;
    }

    public static Double getAsDouble(Map<String, Object> map, String key) {
        var value = map.get(key);
        return value == null ? null : (Double) value;
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> getAsList(Map<String, Object> map, String key) {
        var value = map.get(key);
        return value == null ? null : (List<T>) value;
    }
}
