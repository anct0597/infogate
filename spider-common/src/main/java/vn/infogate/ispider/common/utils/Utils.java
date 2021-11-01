package vn.infogate.ispider.common.utils;

import java.util.Collection;
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
}
