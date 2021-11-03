package vn.infogate.ispider.web.collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import vn.infogate.ispider.storage.model.entity.LocationModel;
import vn.infogate.ispider.storage.model.entity.PriceModel;
import vn.infogate.ispider.web.normalizer.TextNormalizer;
import vn.infogate.ispider.storage.model.types.CalculationUnit;
import vn.infogate.ispider.storage.model.types.PropertyLegalStatus;
import vn.infogate.ispider.storage.model.types.Regex;
import vn.infogate.ispider.web.utils.LocationUtils;
import vn.infogate.ispider.web.utils.VNCharacterUtils;

/**
 * @author anct.
 */
public class CommonCollectors {

    public static LocationModel extractDetailLocation(String raw) {
        var text = VNCharacterUtils.removeAccent(raw).toLowerCase();
        return LocationUtils.getInstance().detectLocation(text);
    }

    public static PriceModel extractPrice(Object raw) {
        var text = TextNormalizer.normalize(String.valueOf(raw));
        var matcher = Regex.PRICE.matcher(TextNormalizer.removeWhitespace(text));
        if (matcher.find()) {
            var unit = matcher.group(2);
            var rawPrice = matcher.group(1);

            return StringUtils.isEmpty(unit)
                    ? extractWithoutUnit(rawPrice)
                    : extractWithCertainUnit(rawPrice, unit);

        }
        return null;
    }

    public static int extractLegalStatus(Object raw) {
        return PropertyLegalStatus.getCode(String.valueOf(raw));
    }

    private static PriceModel extractWithCertainUnit(String rawPrice, String unit) {
        CalculationUnit detectUnit;
        double value = Double.parseDouble(TextNormalizer.replaceCommaByDot(rawPrice));
        if ("tr".equals(unit) || "trieu".equals(unit) || "triệu".equals(unit)) {
            detectUnit = CalculationUnit.TR;
        } else if ("ty".equals(unit) || "tỷ".equals(unit)) {
            detectUnit = CalculationUnit.TY;
        } else {
            detectUnit = CalculationUnit.K;
        }
        return new PriceModel(value, detectUnit);
    }

    private static PriceModel extractWithoutUnit(String rawPrice) {
        double value;
        CalculationUnit detectUnit;
        var textPrice = TextNormalizer.removeCommaDot(rawPrice);
        if (textPrice.length() > 9) {
            detectUnit = CalculationUnit.TY;
            value = Long.parseLong(textPrice) * 1.0 / 1_000_000_000;
        } else if (textPrice.length() > 6) {
            detectUnit = CalculationUnit.TR;
            value = Long.parseLong(textPrice) * 1.0 / 1_000_000;
        } else {
            detectUnit = CalculationUnit.K;
            value = Long.parseLong(textPrice) * 1.0 / 1_000;
        }
        return new PriceModel(value, detectUnit);
    }

    public static Pair<PriceModel, PriceModel> extractRangePrice(Object raw) {
        var text = TextNormalizer.normalize(String.valueOf(raw));
        var matcher = Regex.RANGE_PRICE.matcher(TextNormalizer.removeWhitespace(text));
        if (matcher.find()) {
            var unit = matcher.group(4);
            var lowestPriceRaw = matcher.group(1);
            var highestPriceRaw = matcher.group(3);
            var lPrice = extractWithCertainUnit(lowestPriceRaw, unit);
            var hPrice = extractWithCertainUnit(highestPriceRaw, unit);
            return Pair.of(lPrice, hPrice);
        }
        return null;
    }
}
