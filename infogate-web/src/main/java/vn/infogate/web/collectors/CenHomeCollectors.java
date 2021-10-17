package vn.infogate.web.collectors;

import org.apache.commons.lang3.tuple.Pair;
import vn.infogate.json.JsonFieldCollector;
import vn.infogate.web.model.LocationModel;
import vn.infogate.web.model.PriceModel;
import vn.infogate.web.types.PropertyDirection;
import vn.infogate.web.types.PropertyEquipment;
import vn.infogate.web.types.PropertyType;
import vn.infogate.web.types.Regex;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anct.
 */
public enum CenHomeCollectors implements JsonFieldCollector {

    AREA {
        @Override
        public String extractFrom() {
            return FieldConstants.AREA;
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
            return FieldConstants.AREA;
        }

        @Override
        public String fieldName() {
            return FieldConstants.AREA_UNIT;
        }

        @Override
        public String collect(Object raw) {
            var matcher = Regex.AREA_UNIT.matcher(String.valueOf(raw));
            return matcher.find() ? matcher.group(1) : "m²";
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
            return FieldConstants.PHONE;
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
            return FieldConstants.BED_ROOM;
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
            return FieldConstants.BATH_ROOM;
        }
    },
    DIRECTION {
        @Override
        public Integer collect(Object raw) {
            return PropertyDirection.getCode(String.valueOf(raw));
        }

        @Override
        public String extractFrom() {
            return FieldConstants.DIRECTION;
        }
    },
    UNIT_PRICE {
        @Override
        public PriceModel collect(Object raw) {
            return CommonCollectors.extractPrice(raw);
        }

        @Override
        public String extractFrom() {
            return FieldConstants.UNIT_PRICE;
        }
    },
    RANGE_PRICE {
        @Override
        public Pair<PriceModel, PriceModel> collect(Object raw) {
            return CommonCollectors.extractRangePrice(raw);
        }

        @Override
        public String extractFrom() {
            return FieldConstants.RANGE_PRICE;
        }
    },
    PRICE {
        @Override
        public PriceModel collect(Object raw) {
            return CommonCollectors.extractPrice(raw);
        }

        @Override
        public String extractFrom() {
            return FieldConstants.TOTAL_PRICE;
        }
    },
    PROPERTY_TYPE {
        @Override
        public Integer collect(Object raw) {
            return PropertyType.getCodeFromViName(String.valueOf(raw));
        }

        @Override
        public String extractFrom() {
            return FieldConstants.PROPERTY_TYPE;
        }
    },
    EQUIPMENT {
        @Override
        public Integer collect(Object raw) {
            return PropertyEquipment.getCode(String.valueOf(raw));
        }

        @Override
        public String extractFrom() {
            return FieldConstants.EQUIPMENT;
        }
    },
    LEGAL_STATUS {
        @Override
        public Integer collect(Object raw) {
            return CommonCollectors.extractLegalStatus(raw);
        }

        @Override
        public String extractFrom() {
            return FieldConstants.LEGAL_STATUS;
        }
    },
    DETAIL_LOCATION {
        @Override
        public LocationModel collect(Object raw) {
            return CommonCollectors.extractDetailLocation(String.valueOf(raw));
        }

        @Override
        public String fieldName() {
            return FieldConstants.DETAIL_LOCATION;
        }

        @Override
        public String extractFrom() {
            return FieldConstants.LOCATION;
        }
    };


}
