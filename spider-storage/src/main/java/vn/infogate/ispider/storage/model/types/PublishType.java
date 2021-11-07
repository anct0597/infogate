package vn.infogate.ispider.storage.model.types;

import lombok.Getter;

/**
 * @author an.cantuong
 * created at 11/2/2021
 */
@Getter
public enum PublishType {
    SELL(1, "Cần bán", "Mua nhà"),
    RENT(2, "Cho thuê", "Thuê nhà");

    private final int code;
    private final String[] viNames;

    PublishType(int code, String... viNames) {
        this.code = code;
        this.viNames = viNames;
    }

    public static Integer getCodeFromName(String text) {
        for (var type : values()) {
            for (var name : type.viNames) {
                if (name.equalsIgnoreCase(text)) {
                    return type.code;
                }
            }
        }
        return null;
    }
}
