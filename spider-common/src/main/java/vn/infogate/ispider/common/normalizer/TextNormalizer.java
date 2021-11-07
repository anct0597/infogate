package vn.infogate.ispider.common.normalizer;

import java.text.Normalizer;

/**
 * @author anct.
 */
public class TextNormalizer {

    private TextNormalizer() {
    }

    public static String normalize(String text) {
        return Normalizer.normalize(text.toLowerCase(), Normalizer.Form.NFC);
    }

    /**
     * Replace comma by dot.
     *
     * @param content content.
     * @return replaced string.
     */
    public static String replaceCommaByDot(String content) {
        return content.replaceAll("(\\d+)(,)(\\d+)", "$1.$3");
    }

    public static String removeCommaDot(String content) {
        return content.replaceAll("[.,]+", "");
    }

    public static String removeWhitespace(String content) {
        return content.replaceAll("\\s+", "");
    }
}
