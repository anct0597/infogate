package vn.infogate.ispider.storage.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(of = "name")
public class UField {
    private final String name;

    private final Object value;

    private final AtomicUpdate action;

    private UField(String name, Object value, AtomicUpdate action) {
        this.name = name;
        this.value = value;
        this.action = action;
    }

    public static UField add(String name, Object value) {
        return new UField(name, value, AtomicUpdate.ADD);
    }

    public static UField set(String name, Object value) {
        return new UField(name, value, AtomicUpdate.SET);
    }

    public static UField remove(String name, Object value) {
        return new UField(name, value, AtomicUpdate.REMOVE);
    }

    public static UField inc(String name, Object value) {
        return new UField(name, value, AtomicUpdate.INC);
    }
}
