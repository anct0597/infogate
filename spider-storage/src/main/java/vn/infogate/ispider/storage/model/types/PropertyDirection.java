package vn.infogate.ispider.storage.model.types;

import lombok.Getter;

/**
 * @author anct.
 */
@Getter
public enum PropertyDirection {
    NORTH(1, "Bắc"),
    EAST(2, "Đông"),
    WEST(3, "Tây"),
    SOUTH(4, "Nam"),
    NORTH_EAST(5, "Đông Bắc"),
    SOUTH_EAST(6, "Đông Nam"),
    NORTH_WEST(7, "Tây Bắc"),
    SOUTH_WEST(8, "Tây Nam");


    private final String viName;
    private final int code;

    PropertyDirection(int code, String viName) {
        this.code = code;
        this.viName = viName;
    }

    public static int getCode(String viName) {
        for (var direction : values()) {
            if (direction.viName.equalsIgnoreCase(viName)) {
                return direction.getCode();
            }
        }
        return 0;
    }
}
