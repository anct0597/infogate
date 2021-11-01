package vn.infogate.ispider.utils;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author code4crafter@gmail.com
 * @since 0.5.0
 */
public abstract class ClassUtils {

    public static Set<Field> getFieldsIncludeSuperClass(Class<?> clazz) {
        Set<Field> fields = new LinkedHashSet<>();
        Class<?> current = clazz;
        while (current != null) {
            Field[] currentFields = current.getDeclaredFields();
            Collections.addAll(fields, currentFields);
            current = current.getSuperclass();
        }
        return fields;
    }

}
