package vn.infogate.ispider.web.collectors;

import org.apache.commons.lang3.tuple.Pair;
import vn.infogate.ispider.json.JsonFieldCollector;
import vn.infogate.ispider.storage.model.document.PropertyInfoConstants;
import vn.infogate.ispider.storage.model.entity.LocationModel;
import vn.infogate.ispider.storage.model.entity.PriceModel;
import vn.infogate.ispider.storage.model.types.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anct.
 */
public enum CenHomeCollectors implements JsonFieldCollector {

    AREA {
        @Override
        public String extractFrom() {
            return PropertyInfoConstants.AREA;
        }

        @Override
        public Double collect(Object raw) {
            var matcher = Regex.AREA.matcher(String.valueOf(raw));
            return matcher.find() ? Double.parseDouble(matcher.group(1)) : 0.0;
        }
    },
    AREA_UNIT {
        @Override
        public String extractFrom() {
            return PropertyInfoConstants.AREA;
        }

        @Override
        public String fieldName() {
            return PropertyInfoConstants.AREA_UNIT;
        }

        @Override
        public Integer collect(Object raw) {
            var matcher = Regex.AREA_UNIT.matcher(String.valueOf(raw));
            var areaUnit = matcher.find() ? matcher.group(1) : "m²";
            return AreaUnit.getCode(areaUnit);
        }
    },
    PHONE {
        @Override
        public List<String> collect(Object raw) {
            var matcher = Regex.PHONE.matcher(String.valueOf(raw));
            var phones = new ArrayList<String>(2);
            while (matcher.find()) {
                phones.add(matcher.group(1));
            }
            return phones;
        }

        @Override
        public String extractFrom() {
            return PropertyInfoConstants.PHONES;
        }
    },
    BED_ROOM {
        @Override
        public Integer collect(Object raw) {
            var matcher = Regex.NUMBER.matcher(String.valueOf(raw));
            return matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;
        }

        @Override
        public String extractFrom() {
            return PropertyInfoConstants.BED_ROOMS;
        }
    },
    BATH_ROOM {
        @Override
        public Integer collect(Object raw) {
            var matcher = Regex.NUMBER.matcher(String.valueOf(raw));
            return matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;
        }

        @Override
        public String extractFrom() {
            return PropertyInfoConstants.BATH_ROOMS;
        }
    },
    DIRECTION {
        @Override
        public Integer collect(Object raw) {
            return PropertyDirection.getCode(String.valueOf(raw));
        }

        @Override
        public String extractFrom() {
            return PropertyInfoConstants.DIRECTION;
        }
    },
    UNIT_PRICE {
        @Override
        public PriceModel collect(Object raw) {
            return CommonCollectors.extractPrice(raw);
        }

        @Override
        public String extractFrom() {
            return PropertyInfoConstants.UNIT_PRICE;
        }
    },
    RANGE_PRICE {
        @Override
        public Pair<PriceModel, PriceModel> collect(Object raw) {
            return CommonCollectors.extractRangePrice(raw);
        }

        @Override
        public String extractFrom() {
            return PropertyInfoConstants.RANGE_PRICE;
        }
    },
    PRICE {
        @Override
        public PriceModel collect(Object raw) {
            return CommonCollectors.extractPrice(raw);
        }

        @Override
        public String extractFrom() {
            return PropertyInfoConstants.TOTAL_PRICE;
        }
    },
    PROPERTY_TYPE {
        @Override
        public Integer collect(Object raw) {
            return PropertyType.getCodeFromViName(String.valueOf(raw));
        }

        @Override
        public String extractFrom() {
            return PropertyInfoConstants.PROPERTY_TYPE;
        }
    },
    EQUIPMENT {
        @Override
        public Integer collect(Object raw) {
            return PropertyEquipment.getCode(String.valueOf(raw));
        }

        @Override
        public String extractFrom() {
            return PropertyInfoConstants.EQUIPMENT;
        }
    },
    LEGAL_STATUS {
        @Override
        public Integer collect(Object raw) {
            return CommonCollectors.extractLegalStatus(raw);
        }

        @Override
        public String extractFrom() {
            return PropertyInfoConstants.LEGAL_STATUS;
        }
    },
    DETAIL_LOCATION {
        @Override
        public LocationModel collect(Object raw) {
            return CommonCollectors.extractDetailLocation(String.valueOf(raw));
        }

        @Override
        public String fieldName() {
            return PropertyInfoConstants.DETAIL_LOCATION;
        }

        @Override
        public String extractFrom() {
            return PropertyInfoConstants.LOCATION;
        }
    }
}
