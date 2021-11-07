package vn.infogate.ispider.storage.model.types;

import lombok.Getter;

/**
 * @author anct.
 */
@Getter
public enum PropertyEquipment {
    BASIC(1, "Nội thất cơ bản"),
    FULL(2, "Nội thất đầy đủ"),
    LUXURY(3, "Nội thất cao cấp");

    private final int code;
    private final String viName;


    PropertyEquipment(int code, String viName) {
        this.code = code;
        this.viName = viName;
    }

    public static Integer getCode(String viName) {
        for (var type : values()) {
            if (type.getViName().equalsIgnoreCase(viName)) {
                return type.getCode();
            }
        }
        return null;
    }
}
