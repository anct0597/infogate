package vn.infogate.ispider.web.collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import vn.infogate.ispider.common.normalizer.TextNormalizer;
import vn.infogate.ispider.common.utils.VNCharacterUtils;
import vn.infogate.ispider.storage.model.entity.LocationModel;
import vn.infogate.ispider.storage.model.entity.PriceModel;
import vn.infogate.ispider.storage.model.types.CalculationUnit;
import vn.infogate.ispider.storage.model.types.Regex;
import vn.infogate.ispider.storage.utils.LocationUtils;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author anct.
 */
public class CommonCollectors {

    public static Integer extractAsInt(Object raw) {
        return extractAsInt(raw, null);
    }

    public static Integer extractAsInt(Object raw, Integer defaultValue) {
        var matcher = Regex.NUMBER.matcher(String.valueOf(raw));
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : defaultValue;
    }

    public static Double extractAsDouble(Object raw) {
        var matcher = Regex.NUMBER.matcher(String.valueOf(raw));
        return matcher.find() ? Double.parseDouble(matcher.group(1)) : 0.0;
    }

    public static Integer check(Object raw, Predicate<Object> predicate) {
        return predicate.test(raw) ? 1 : null;
    }

    public static List<String> extractPhones(Object... raws) {
        var phones = new ArrayList<String>(raws.length);
        for (var raw : raws) {
            var result = extractPhone(raw);
            if (StringUtils.isNotEmpty(result)) phones.add(result);
        }
        return phones;
    }

    public static String extractPhone(Object raw) {
        var text = String.valueOf(raw);
        var rawPhone = TextNormalizer.removeCommaDot(text).toLowerCase();
        var matcher = Regex.PHONE.matcher(rawPhone);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return rawPhone.contains("xxx") ? rawPhone : null;
    }

    public static LocationModel extractDetailLocation(String raw) {
        var normalizedText = Normalizer.normalize(raw.toLowerCase(), Normalizer.Form.NFC);
        var text = VNCharacterUtils.removeAccent(normalizedText);
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
