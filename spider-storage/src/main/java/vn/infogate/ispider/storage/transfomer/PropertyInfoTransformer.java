package vn.infogate.ispider.storage.transfomer;

import lombok.extern.slf4j.Slf4j;
import vn.infogate.ispider.common.utils.UUIdUtils;
import vn.infogate.ispider.common.utils.Utils;
import vn.infogate.ispider.storage.model.document.PropertyInfoConstants;
import vn.infogate.ispider.storage.model.document.PropertyInfoDoc;
import vn.infogate.ispider.storage.model.entity.LocationModel;

import java.util.Map;

/**
 * @author an.cantuong
 * created at 11/2/2021
 */
@Slf4j
public class PropertyInfoTransformer {

    public static PropertyInfoDoc toPropertyInfoDoc(String url, Map<String, Object> fieldValues) {
        log.debug("url: {},field values: {}", url, fieldValues);
        var chInfo = new PropertyInfoDoc();
        chInfo.setId(UUIdUtils.generateSimpleUUID());
        chInfo.setUrl(url);
        chInfo.setTitle(Utils.getAsStr(fieldValues, PropertyInfoConstants.TITLE));
        chInfo.setShortSummary(Utils.getAsStr(fieldValues, PropertyInfoConstants.SHORT_SUMMARY));
        chInfo.setArea(Utils.getAsDouble(fieldValues, PropertyInfoConstants.AREA));
        chInfo.setUnitArea(Utils.getAsInt(fieldValues, PropertyInfoConstants.AREA_UNIT));

        chInfo.setBathRooms(Utils.getAsInt(fieldValues, PropertyInfoConstants.BATH_ROOMS));
        chInfo.setBedRooms(Utils.getAsInt(fieldValues, PropertyInfoConstants.BED_ROOMS));

        chInfo.setDirection(Utils.getAsInt(fieldValues, PropertyInfoConstants.DIRECTION));
        chInfo.setEquipment(Utils.getAsInt(fieldValues, PropertyInfoConstants.EQUIPMENT));
        chInfo.setLegalStatus(Utils.getAsInt(fieldValues, PropertyInfoConstants.LEGAL_STATUS));
        chInfo.setLocation(Utils.getAsStr(fieldValues, PropertyInfoConstants.LOCATION));
        chInfo.setImages(Utils.getAsList(fieldValues, PropertyInfoConstants.IMAGES));
        chInfo.setPhones(Utils.getAsList(fieldValues, PropertyInfoConstants.PHONES));

        chInfo.setInvestor(Utils.getAsStr(fieldValues, PropertyInfoConstants.INVESTOR));
        chInfo.setSource(Utils.getAsStr(fieldValues, PropertyInfoConstants.SOURCE_INFO));
        chInfo.setPublishType(Utils.getAsInt(fieldValues, PropertyInfoConstants.PUBLISH_TYPE));
        chInfo.setPublisher(Utils.getAsStr(fieldValues, PropertyInfoConstants.PUBLISHER));
        chInfo.setPublishDate(Utils.getAsLong(fieldValues, PropertyInfoConstants.PUBLISH_DATE));
        chInfo.setCrawledTime(Utils.getAsLong(fieldValues, PropertyInfoConstants.CRAWLED_TIME));
        chInfo.setReporters(Utils.getAsList(fieldValues, PropertyInfoConstants.REPORTERS));
        chInfo.setReportReasons(Utils.getAsList(fieldValues, PropertyInfoConstants.REPORT_REASONS));

        //Set price
        chInfo.setTotalPrice(PriceTransformer.getTotalPriceValue(fieldValues));
        chInfo.setTotalPriceCalUnit(PriceTransformer.getTotalPriceCalUnit(fieldValues));
        chInfo.setUnitPrice(PriceTransformer.getUnitPriceValue(fieldValues));
        chInfo.setUnitPriceCalUnit(PriceTransformer.getUnitPriceCalUnit(fieldValues));

        // Set detail location
        var detailLocation = (LocationModel) fieldValues.get(PropertyInfoConstants.DETAIL_LOCATION);
        if (detailLocation != null) {
            chInfo.setProvinceCode(detailLocation.getProvinceCode());
            chInfo.setDistrictCode(detailLocation.getDistrictCode());
            chInfo.setWardCode(detailLocation.getWardCode());
        }

        return chInfo;
    }
}
