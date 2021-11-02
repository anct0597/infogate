package vn.infogate.ispider.storage.model.types;

import lombok.Getter;

/**
 * @author anct.
 */
@Getter
public enum PropertyLegalStatus {
    SD_LD(1, "Sổ đỏ lâu dài");

    private final int code;
    private final String viName;

    PropertyLegalStatus(int code, String viName) {
        this.code = code;
        this.viName = viName;
    }

    public static int getCode(String text) {
        for (var type : values()) {
            if (type.getViName().equalsIgnoreCase(text)) {
                return type.getCode();
            }
        }
        return 0;
    }
}
