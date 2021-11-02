package vn.infogate.ispider.storage.model.types;

import lombok.Getter;

/**
 * @author an.cantuong
 * created at 11/2/2021
 */
@Getter
public enum PublishType {
    OWNER(1, "Chính chủ"),
    BROKER(2, "Môi giới");

    private final int code;
    private final String viName;

    PublishType(int code, String viName) {
        this.code = code;
        this.viName = viName;
    }
}
