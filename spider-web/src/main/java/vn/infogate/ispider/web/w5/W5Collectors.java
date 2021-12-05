package vn.infogate.ispider.web.w5;

import vn.infogate.ispider.common.normalizer.TextNormalizer;
import vn.infogate.ispider.extension.json.JsonFieldCollector;
import vn.infogate.ispider.storage.model.document.PropertyInfoConstants;
import vn.infogate.ispider.storage.model.entity.LocationModel;
import vn.infogate.ispider.storage.model.entity.PriceModel;
import vn.infogate.ispider.storage.model.types.AreaUnit;
import vn.infogate.ispider.storage.model.types.PropertyLegalStatus;
import vn.infogate.ispider.storage.model.types.PropertyType;
import vn.infogate.ispider.storage.model.types.PublishType;
import vn.infogate.ispider.storage.model.types.PublisherType;
import vn.infogate.ispider.storage.model.types.Regex;
import vn.infogate.ispider.web.collectors.CommonCollectors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

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

        private final Pattern PATTERN = Pattern.compile("varimageUrls=([\\w:\\-\\.\\/\\/\\/,]+)*");

        @Override
        public List<String> collect(Object raw) {
            var text = TextNormalizer.removeWhitespace(String.valueOf(raw));
            text = TextNormalizer.removeQuote(text);
            text = TextNormalizer.removeBracket(text);

            var matcher = PATTERN.matcher(text);

            var images = new ArrayList<String>(10);
            if (matcher.find()) {
                var imageGroup = matcher.group(1);
                images.addAll(Arrays.asList(imageGroup.split(",")));
            }
            return images;
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
