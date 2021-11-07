package vn.infogate.ispider.storage.model.types;

import lombok.Getter;

/**
 * @author an.cantuong
 * created at 11/2/2021
 */
@Getter
public enum PublisherType {
    OWNER(1, "Chính chủ"),
    BROKER(2, "Môi giới");

    private final int code;
    private final String viName;

    PublisherType(int code, String viName) {
        this.code = code;
        this.viName = viName;
    }

    public static Integer getCode(String text) {
        for (var type : values()) {
            if (type.getViName().equalsIgnoreCase(text)) {
                return type.getCode();
            }
        }
        return BROKER.code;
    }
}
