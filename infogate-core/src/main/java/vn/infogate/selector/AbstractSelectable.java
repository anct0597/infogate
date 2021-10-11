package vn.infogate.selector;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author code4crafer@gmail.com
 * @since 0.5.2
 */
public abstract class AbstractSelectable implements Selectable {

    protected abstract List<String> getSourceTexts();

    @Override
    public Selectable css(String selector) {
        return $(selector);
    }

    @Override
    public Selectable css(String selector, String attrName) {
        return $(selector, attrName);
    }

    protected Selectable select(Selector selector, List<String> strings) {
        var results = new ArrayList<String>();
        for (var string : strings) {
            var result = selector.select(string);
            if (StringUtils.isNotEmpty(result)) {
                results.add(result);
            }
        }
        return new PlainText(results);
    }

    protected Selectable selectList(Selector selector, List<String> strings) {
        var results = new ArrayList<String>();
        for (var string : strings) {
            results.addAll(selector.selectList(string));
        }
        return new PlainText(results);
    }

    @Override
    public List<String> all() {
        return getSourceTexts();
    }

    @Override
    public Selectable jsonPath(String jsonPath) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String get() {
        var allSourceText = all();
        return CollectionUtils.isNotEmpty(allSourceText) ? allSourceText.get(0) : null;
    }

    @Override
    public Selectable select(Selector selector) {
        return select(selector, getSourceTexts());
    }

    @Override
    public Selectable selectList(Selector selector) {
        return selectList(selector, getSourceTexts());
    }

    @Override
    public Selectable regex(String regex) {
        RegexSelector regexSelector = Selectors.regex(regex);
        return selectList(regexSelector, getSourceTexts());
    }

    @Override
    public Selectable regex(String regex, int group) {
        RegexSelector regexSelector = Selectors.regex(regex, group);
        return selectList(regexSelector, getSourceTexts());
    }

    @Override
    public Selectable replace(String regex, String replacement) {
        ReplaceSelector replaceSelector = new ReplaceSelector(regex, replacement);
        return select(replaceSelector, getSourceTexts());
    }

    public String getFirstSourceText() {
        var sourceTexts = getSourceTexts();
        return CollectionUtils.isNotEmpty(sourceTexts) ? sourceTexts.get(0) : null;
    }

    @Override
    public String toString() {
        return get();
    }

    @Override
    public boolean match() {
        return CollectionUtils.isNotEmpty(getSourceTexts());
    }
}
