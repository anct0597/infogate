package vn.infogate.ispider.storage.model.types;

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
    LIVING_HOUSE(5, "Nhà ở", "Nhà ngõ"),
    STREET_HOUSE(6, "Nhà mặt tiền", "Nhà mặt phố"),
    VILLA(7, "Nhà liền kề & Biệt thự", "Biệt thự, nhà liền kề"),
    SHOP_HOUSE(8, "Shophouse", "Shop, kiot, quán", "Kiot"),
    LIVING_LAND(9, "Đất ở"),
    SELL_LAND(10, "Đất nền dự án"),
    RESIDENTIAL_LAND(10, "Đất thổ cư", "Đất ở", "Đất thổ cư, đất ở"),
    AGRI_LAND(11, "Đất nông nghiệp", "Đất nông, lâm nghiệp"),
    FARM_LAND(12, "Trang trại"),
    HOTEL_MOTEL(13, "Khách sạn"),
    OFFICE(13, "Văn phòng"),
    WARE_HOUSE(13, "Kho, xưởng", "Kho", "Xưởng");

    private final int code;
    private final String[] viNames;

    PropertyType(int code, String... viNames) {
        this.code = code;
        this.viNames = viNames;
    }

    public static Integer getCodeFromViName(String text) {
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
