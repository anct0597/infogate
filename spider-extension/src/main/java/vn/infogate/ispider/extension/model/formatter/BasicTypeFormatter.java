package vn.infogate.ispider.extension.model.formatter;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author code4crafter@gmail.com
 * @since 0.3.2
 */
public abstract class BasicTypeFormatter<T> implements ObjectFormatter<T> {

    @Override
    public void initParam(String[] extra) {
    }

    @Override
    public T format(String raw) throws Exception {
        return StringUtils.isNotEmpty(raw) ? formatTrimmed(raw.trim()) : null;
    }

    protected abstract T formatTrimmed(String raw) throws Exception;

    public static final List<Class<? extends ObjectFormatter>> basicTypeFormatters = Arrays.asList(IntegerFormatter.class,
            LongFormatter.class, DoubleFormatter.class, FloatFormatter.class, ShortFormatter.class,
            CharacterFormatter.class, ByteFormatter.class, BooleanFormatter.class);

    public static Class<?> detectBasicClass(Class<?> type) {
        if (type.equals(Integer.TYPE) || type.equals(Integer.class)) {
            return Integer.class;
        } else if (type.equals(Long.TYPE) || type.equals(Long.class)) {
            return Long.class;
        } else if (type.equals(Double.TYPE) || type.equals(Double.class)) {
            return Double.class;
        } else if (type.equals(Float.TYPE) || type.equals(Float.class)) {
            return Float.class;
        } else if (type.equals(Short.TYPE) || type.equals(Short.class)) {
            return Short.class;
        } else if (type.equals(Character.TYPE) || type.equals(Character.class)) {
            return Character.class;
        } else if (type.equals(Byte.TYPE) || type.equals(Byte.class)) {
            return Byte.class;
        } else if (type.equals(Boolean.TYPE) || type.equals(Boolean.class)) {
            return Boolean.class;
        }
        return type;
    }

    public static class IntegerFormatter extends BasicTypeFormatter<Integer> {
        @Override
        public Integer formatTrimmed(String raw) {
            return Integer.parseInt(raw);
        }

        @Override
        public Class<Integer> clazz() {
            return Integer.class;
        }
    }

    public static class LongFormatter extends BasicTypeFormatter<Long> {
        @Override
        public Long formatTrimmed(String raw) {
            return Long.parseLong(raw);
        }

        @Override
        public Class<Long> clazz() {
            return Long.class;
        }
    }

    public static class DoubleFormatter extends BasicTypeFormatter<Double> {
        @Override
        public Double formatTrimmed(String raw) throws Exception {
            return Double.parseDouble(raw);
        }

        @Override
        public Class<Double> clazz() {
            return Double.class;
        }
    }

    public static class FloatFormatter extends BasicTypeFormatter<Float> {
        @Override
        public Float formatTrimmed(String raw) {
            return Float.parseFloat(raw);
        }

        @Override
        public Class<Float> clazz() {
            return Float.class;
        }
    }

    public static class ShortFormatter extends BasicTypeFormatter<Short> {
        @Override
        public Short formatTrimmed(String raw) {
            return Short.parseShort(raw);
        }

        @Override
        public Class<Short> clazz() {
            return Short.class;
        }
    }

    public static class CharacterFormatter extends BasicTypeFormatter<Character> {
        @Override
        public Character formatTrimmed(String raw) {
            return raw.charAt(0);
        }

        @Override
        public Class<Character> clazz() {
            return Character.class;
        }
    }

    public static class ByteFormatter extends BasicTypeFormatter<Byte> {
        @Override
        public Byte formatTrimmed(String raw) {
            return Byte.parseByte(raw, 10);
        }

        @Override
        public Class<Byte> clazz() {
            return Byte.class;
        }
    }

    public static class BooleanFormatter extends BasicTypeFormatter<Boolean> {
        @Override
        public Boolean formatTrimmed(String raw) {
            return Boolean.parseBoolean(raw);
        }

        @Override
        public Class<Boolean> clazz() {
            return Boolean.class;
        }
    }


}
