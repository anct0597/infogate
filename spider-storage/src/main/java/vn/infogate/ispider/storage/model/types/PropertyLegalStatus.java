package vn.infogate.ispider.storage.model.types;

import lombok.Getter;

/**
 * @author anct.
 */
@Getter
public enum PropertyLegalStatus {
    SD_LD(1, "Sổ đỏ lâu dài"),
    SH(2, "Sổ đỏ lâu dài"),
    SD_SH(3, "Sổ hồng/ Sổ đỏ"),
    GT_HL(3, "Giấy tờ hợp lệ");

    private final int code;
    private final String viName;

    PropertyLegalStatus(int code, String viName) {
        this.code = code;
        this.viName = viName;
    }

    public static Integer getCode(String text) {
        for (var type : values()) {
            if (type.getViName().equalsIgnoreCase(text)) {
                return type.getCode();
            }
        }
        return null;
    }
}
