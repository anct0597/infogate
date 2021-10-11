package vn.infogate.utils;

import vn.infogate.model.annotation.ExtractBy;
import vn.infogate.selector.CssSelector;
import vn.infogate.selector.JsonPathSelector;
import vn.infogate.selector.RegexSelector;
import vn.infogate.selector.Selector;
import vn.infogate.selector.XpathSelector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tools for annotation converting. <br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.1
 */
public class ExtractorUtils {

    public static Selector getSelector(ExtractBy extractBy) {
        String value = extractBy.value();
        Selector selector;
        switch (extractBy.type()) {
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

    public static List<Selector> getSelectors(ExtractBy[] extractByArr) {
        if (extractByArr == null) return Collections.emptyList();
        List<Selector> selectors = new ArrayList<>(extractByArr.length);
        for (ExtractBy extractBy : extractByArr) {
            selectors.add(getSelector(extractBy));
        }
        return selectors;
    }
}
