package vn.infogate.ispider.selector;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.util.Collections;
import java.util.List;

/**
 * @author anct.
 */
public abstract class BaseElementSelector implements Selector, ElementSelector {

    @Override
    public String select(String text) {
        return StringUtils.isNotEmpty(text) ? select(Jsoup.parse(text)) : null;
    }

    @Override
    public List<String> selectList(String text) {
        return StringUtils.isEmpty(text) ? Collections.emptyList() : selectList(Jsoup.parse(text));
    }

    public Element selectElement(String text) {
        return StringUtils.isNotEmpty(text) ? selectElement(Jsoup.parse(text)) : null;
    }

    public List<Element> selectElements(String text) {
        return StringUtils.isNotEmpty(text) ? selectElements(Jsoup.parse(text)) : null;

    }

    public abstract Element selectElement(Element element);

    public abstract List<Element> selectElements(Element element);

    public abstract boolean hasAttribute();

}
