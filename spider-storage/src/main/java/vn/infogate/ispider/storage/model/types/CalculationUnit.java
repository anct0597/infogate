package vn.infogate.ispider.storage.model.types;

import lombok.Getter;

/**
 * @author anct.
 */
@Getter
public enum CalculationUnit {
    TY(1, "tỷ"),
    TR(2, "triệu"),
    K(3, "nghìn");

    private final int code;
    private final String viName;

    CalculationUnit(int code, String viName) {
        this.code = code;
        this.viName = viName;
    }
}
