package vn.infogate.ispider.storage.solr.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.beans.BindingException;

import java.lang.invoke.*;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Getter
@Setter
@SuppressWarnings({"rawtypes", "unchecked"})
public class SField {
    private Field field;

    private Class<?> type;

    private String name;

    private boolean child;

    private Class<?> childType;

    private Function getter;

    private BiConsumer setter;

    public SField(AccessibleObject member) {
        if (member instanceof Field) {
            this.field = (Field) member;
            var annotation = field.getAnnotation(org.apache.solr.client.solrj.beans.Field.class);
            this.child = annotation.child();
            this.name = annotation.value();
            this.type = getFieldType();
            this.getter = makeGetter();
            this.setter = makeSetter();
            if (child) this.childType = resolveChildClass();
        } else throw new BindingException("@Field should annotated on field.");
    }

    /**
     * Get valid field type.
     * Field type should be an object.
     */
    private Class<?> getFieldType() {
        var fieldType = field.getType();
        if (fieldType.isAssignableFrom(boolean.class)
                || fieldType.isAssignableFrom(int.class)
                || fieldType.isAssignableFrom(double.class)
                || fieldType.isAssignableFrom(float.class)
                || fieldType.isAssignableFrom(char.class)
                || fieldType.isAssignableFrom(short.class)
                || fieldType.isAssignableFrom(long.class)) {
            throw new BindingException("Field should be a wrapper class instead primitive type.");
        }
        return fieldType;
    }

    public void inject(Object bean, Object fieldValue) {
        set(bean, fieldValue);
    }

    private Class<?> resolveChildClass() {
        var actualTypeArguments = ((ParameterizedType) (field).getGenericType()).getActualTypeArguments();
        return (Class<?>) actualTypeArguments[0];
    }

    public void set(Object bean, Object value) {
        setter.accept(bean, value);
    }

    public Object get(Object obj) {
        return getter.apply(obj);
    }

    private Function makeGetter() {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            CallSite site = LambdaMetafactory.metafactory(lookup, "apply",
                    MethodType.methodType(Function.class),
                    MethodType.methodType(Object.class, Object.class),
                    lookupGetMethod(lookup),
                    MethodType.methodType(type, field.getDeclaringClass()));
            return (Function) site.getTarget().invokeExact();
        } catch (Throwable e) {
            throw new BindingException("Can not find getter of " + field.getName(), e);
        }
    }

    private MethodHandle lookupGetMethod(MethodHandles.Lookup lookup) throws NoSuchMethodException, IllegalAccessException {
        try {
            return lookup.findVirtual(field.getDeclaringClass(), guessGetterMethodName(), MethodType.methodType(type));
        } catch (Exception e) {
            var methodName = String.format("%s%s", "get", StringUtils.capitalize(field.getName()));
            return lookup.findVirtual(field.getDeclaringClass(), methodName, MethodType.methodType(type));
        }
    }

    private BiConsumer makeSetter() {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            CallSite site = LambdaMetafactory.metafactory(lookup, "accept",
                    MethodType.methodType(BiConsumer.class),
                    MethodType.methodType(void.class, Object.class, Object.class),
                    lookup.findVirtual(field.getDeclaringClass(), guessSetterMethodName(), MethodType.methodType(void.class, type)),
                    MethodType.methodType(void.class, field.getDeclaringClass(), type));
            return (BiConsumer) site.getTarget().invokeExact();
        } catch (Throwable e) {
            throw new BindingException("Can not find getter of " + field.getName(), e);
        }
    }

    private String guessGetterMethodName() {
        var prefix = type.isAssignableFrom(Boolean.class) ? "is" : "get";
        return String.format("%s%s", prefix, StringUtils.capitalize(field.getName()));
    }

    private String guessSetterMethodName() {
        return String.format("%s%s", "set", StringUtils.capitalize(field.getName()));
    }
}