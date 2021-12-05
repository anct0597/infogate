package vn.infogate.ispider.extension.model.formatter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author code4crafter@gmail.com
 * @since 0.3.2
 */
@SuppressWarnings("rawtypes")
public class ObjectFormatters {

    private static final Map<Class, Class<? extends ObjectFormatter>> formatterMap = new ConcurrentHashMap<>();

    static {
        for (var basicTypeFormatter : BasicTypeFormatter.basicTypeFormatters) {
            put(basicTypeFormatter);
        }
        put(DateFormatter.class);
    }

    public static void put(Class<? extends ObjectFormatter> objectFormatter) {
        try {
            formatterMap.put(objectFormatter.getDeclaredConstructor().newInstance().clazz(), objectFormatter);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Class<? extends ObjectFormatter> get(Class<?> clazz) {
        return formatterMap.get(clazz);
    }
}
