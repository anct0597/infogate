package vn.infogate.ispider.storage.model.types;

import lombok.Getter;

/**
 * @author anct.
 */
@Getter
public enum AreaUnit {

    M2("m²");

    private final String name;

    AreaUnit(String name) {
        this.name = name;
    }
}
