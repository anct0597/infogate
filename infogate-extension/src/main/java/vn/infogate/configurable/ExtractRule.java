package vn.infogate.configurable;

import lombok.Getter;
import lombok.Setter;
import vn.infogate.selector.JsonPathSelector;
import vn.infogate.selector.Selector;

import static vn.infogate.selector.Selectors.$;
import static vn.infogate.selector.Selectors.regex;
import static vn.infogate.selector.Selectors.xpath;


/**
 * @author code4crafter@gmail.com
 */
@Getter
@Setter
public class ExtractRule {

    private String fieldName;

    private ExpressionType expressionType;

    private String expressionValue;

    private String[] expressionParams;

    private boolean multi = false;

    private volatile Selector selector;

    private boolean notNull = false;

    public Selector getSelector() {
        if (selector == null) {
            synchronized (this) {
                if (selector == null) {
                    selector = compileSelector();
                }
            }
        }
        return selector;
    }

    private Selector compileSelector() {
        switch (expressionType) {
            case Css:
                if (expressionParams.length >= 1) {
                    return $(expressionValue, expressionParams[0]);
                } else {
                    return $(expressionValue);
                }
            case Regex:
                if (expressionParams.length >= 1) {
                    return regex(expressionValue, Integer.parseInt(expressionParams[0]));
                } else {
                    return regex(expressionValue);
                }
            case JsonPath:
                return new JsonPathSelector(expressionValue);
            default:
                return xpath(expressionValue);
        }
    }
}
