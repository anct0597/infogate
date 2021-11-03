package vn.infogate.ispider.storage.transfomer;

import lombok.extern.slf4j.Slf4j;
import vn.infogate.ispider.storage.model.document.PropertyInfoConstants;
import vn.infogate.ispider.storage.model.entity.PriceModel;

import java.util.Map;

/**
 * @author an.cantuong
 * created at 11/3/2021
 */
@Slf4j
public class PriceTransformer {

    public static Double getUnitPriceValue(Map<String, Object> map) {
        return getPriceValue(map, PropertyInfoConstants.UNIT_PRICE);
    }

    public static Double getTotalPriceValue(Map<String, Object> map) {
        return getPriceValue(map, PropertyInfoConstants.TOTAL_PRICE);
    }

    public static Integer getUnitPriceCalUnit(Map<String, Object> map) {
        return getCalUnit(map, PropertyInfoConstants.UNIT_PRICE);
    }

    public static Integer getTotalPriceCalUnit(Map<String, Object> map) {
        return getCalUnit(map, PropertyInfoConstants.TOTAL_PRICE);
    }

    private static Integer getCalUnit(Map<String, Object> map, String key) {
        var priceModel = (PriceModel) map.get(key);
        return priceModel == null ? null : priceModel.getUnit().getCode();
    }

    private static Double getPriceValue(Map<String, Object> map, String key) {
        var priceModel = (PriceModel) map.get(key);
        return priceModel == null ? null : priceModel.getValue();
    }
}
