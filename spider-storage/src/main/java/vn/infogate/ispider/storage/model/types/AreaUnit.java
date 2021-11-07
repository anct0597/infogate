package vn.infogate.ispider.storage.model.types;

import lombok.Getter;

/**
 * @author anct.
 */
@Getter
public enum AreaUnit {

    M2(1, "m²");

    private final int code;
    private final String name;

    AreaUnit(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static Integer getCode(String text) {
        for (var type : values()) {
            if (type.getName().equalsIgnoreCase(text)) {
                return type.getCode();
            }
        }
        return AreaUnit.M2.code;
    }
}
