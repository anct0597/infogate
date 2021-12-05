package vn.infogate.ispider.extension.utils;

import vn.infogate.ispider.core.selector.CssSelector;
import vn.infogate.ispider.core.selector.JsonPathSelector;
import vn.infogate.ispider.core.selector.RegexSelector;
import vn.infogate.ispider.core.selector.Selector;
import vn.infogate.ispider.core.selector.XpathSelector;
import vn.infogate.ispider.extension.model.Extractor;
import vn.infogate.ispider.extension.model.annotation.ExtractBy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author anct.
 */
public class ExtractorUtils {

    public static Selector getSelector(String value, Extractor.Type type) {
        Selector selector;
        switch (type) {
            case Css:
                selector = new CssSelector(value);
                break;
            case Regex:
                selector = new RegexSelector(value);
                break;
            case JsonPath:
                selector = new JsonPathSelector(value);
                break;
            default:
                selector = new XpathSelector(value);
        }
        return selector;
    }

    public static Selector getSelector(ExtractBy extractBy) {
        return getSelector(extractBy.value(), extractBy.type());
    }

    public static List<Selector> getSelectors(ExtractBy[] extractByArr) {
        if (extractByArr == null) return Collections.emptyList();
        List<Selector> selectors = new ArrayList<>(extractByArr.length);
        for (ExtractBy extractBy : extractByArr) {
            selectors.add(getSelector(extractBy));
        }
        return selectors;
    }
}
