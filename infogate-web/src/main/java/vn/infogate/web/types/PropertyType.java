package vn.infogate.web.types;

import lombok.Getter;

/**
 * @author anct.
 */
@Getter
public enum PropertyType {
    APARTMENT(1, "Chung cư"),
    APARTMENT_MINI(2, "Chung cư mini"),
    COMM_HOUSE(3, "Tập thể"),
    CONDO_TEL(4, "Condotel"),
    LIVING_HOUSE(5, "Nhà ở"),
    STREET_HOUSE(6, "Nhà mặt tiền"),
    VILLA(7, "Nhà liền kề & Biệt thự"),
    SHOP_HOUSE(8, "Shophouse"),
    LIVING_LAND(9, "Đất ở"),
    SELL_LAND(10, "Đất nền dự án"),
    AGRI_LAND(11, "Đất nông nghiệp"),
    FARM_LAND(12, "Trang trại"),
    HOTEL_MOTEL(13, "Khách sạn");

    private final int code;
    private final String viName;

    PropertyType(int code, String viName) {
        this.code = code;
        this.viName = viName;
    }

    public static int getCodeFromViName(String viName) {
        for (var type : values()) {
            if (type.getViName().equalsIgnoreCase(viName)) {
                return type.getCode();
            }
        }
        return 0;
    }
}
