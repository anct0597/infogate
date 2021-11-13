package vn.infogate.ispider.web.w5;

import org.jsoup.Jsoup;
import vn.infogate.ispider.json.JsonFieldCollector;
import vn.infogate.ispider.selector.JsonPathSelector;
import vn.infogate.ispider.storage.model.document.PropertyInfoConstants;
import vn.infogate.ispider.storage.model.entity.LocationModel;
import vn.infogate.ispider.storage.model.entity.PriceModel;
import vn.infogate.ispider.storage.model.types.*;
import vn.infogate.ispider.web.collectors.CommonCollectors;

import java.util.List;

/**
 * @author anct.
 */
public enum W5Collectors implements JsonFieldCollector {

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
    LIVING_AREA {
        @Override
        public String extractFrom() {
            return PropertyInfoConstants.LIVING_AREA;
        }

        @Override
        public Double collect(Object raw) {
            return CommonCollectors.extractAsDouble(raw);
        }
    },
    WIDTH {
        @Override
        public String extractFrom() {
            return PropertyInfoConstants.WIDTH;
        }

        @Override
        public Double collect(Object raw) {
            return CommonCollectors.extractAsDouble(raw);
        }
    },
    LENGTH {
        @Override
        public String extractFrom() {
            return PropertyInfoConstants.LENGTH;
        }

        @Override
        public Double collect(Object raw) {
            return CommonCollectors.extractAsDouble(raw);
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
            return CommonCollectors.extractPhones(raw);
        }

        @Override
        public String extractFrom() {
            return PropertyInfoConstants.PHONES;
        }
    },
    BED_ROOM {
        @Override
        public Integer collect(Object raw) {
            return CommonCollectors.extractAsInt(raw);
        }

        @Override
        public String extractFrom() {
            return PropertyInfoConstants.BED_ROOMS;
        }
    },
    FLOORS {
        @Override
        public Integer collect(Object raw) {
            return CommonCollectors.extractAsInt(raw);
        }

        @Override
        public String extractFrom() {
            return PropertyInfoConstants.FLOORS;
        }
    },
    TOILETS {
        @Override
        public Integer collect(Object raw) {
            return CommonCollectors.extractAsInt(raw);
        }

        @Override
        public String extractFrom() {
            return PropertyInfoConstants.TOILET;
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
    IMAGES {
        @Override
        public List<String> collect(Object raw) {
            var replacedText =  Jsoup.parse(String.valueOf(raw)).data();
            return new JsonPathSelector("$..ad.images").selectList(replacedText);
        }

        @Override
        public String extractFrom() {
            return PropertyInfoConstants.IMAGES;
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
    LEGAL_STATUS {
        @Override
        public Integer collect(Object raw) {
            return PropertyLegalStatus.getCode(String.valueOf(raw));
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
    },
    PUBLISHER_TYPE {
        @Override
        public Integer collect(Object raw) {
            return PublisherType.getCode(String.valueOf(raw));
        }

        @Override
        public String extractFrom() {
            return PropertyInfoConstants.PUBLISHER_TYPE;
        }
    },
    PUBLISH_TYPE {
        @Override
        public Integer collect(Object raw) {
            return PublishType.getCodeFromName(String.valueOf(raw));
        }

        @Override
        public String extractFrom() {
            return PropertyInfoConstants.PUBLISH_TYPE;
        }
    },
    PUBLISH_DATE {
        @Override
        public Long collect(Object raw) {
            return CommonCollectors.extractDate(raw);
        }

        @Override
        public String extractFrom() {
            return PropertyInfoConstants.PUBLISH_DATE;
        }
    }
}
