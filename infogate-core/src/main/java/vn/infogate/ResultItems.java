package vn.infogate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
public class ResultItems {

    private boolean skip;
    private Request request;
    private final Map<String, Object> fields;

    public ResultItems() {
        this.fields = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        var value = this.fields.get(key);
        return value == null ? null : (T) value;
    }

    /**
     * Put to field.
     *
     * @param key   field name.
     * @param value field value.
     */
    public <T> ResultItems put(String key, T value) {
        this.fields.put(key, value);
        return this;
    }

    /**
     * Put a map into fields.
     */
    public void initAll(Map<String, Object> fields) {
        this.fields.putAll(fields);
    }

    /**
     * Check field is empty.
     */
    public boolean isEmpty() {
        return this.fields.isEmpty();
    }
}
