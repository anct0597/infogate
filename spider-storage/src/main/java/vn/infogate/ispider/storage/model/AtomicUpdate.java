package vn.infogate.ispider.storage.model;

import lombok.Getter;

public enum AtomicUpdate {
    SET("set"),
    ADD("add"),
    REMOVE("remove"),
    INC("inc");

    @Getter
    private final String name;

    AtomicUpdate(String name) {
        this.name = name;
    }
}
