package vn.infogate.ispider.selector;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import us.codecraft.xsoup.XTokenQueue;
import vn.infogate.ispider.common.objectmapper.ObjectMapperFactory;

import java.util.List;

/**
 * parse json
 * @author code4crafter@gmail.com
 * @since 0.5.0
 */
public class Json extends PlainText {

    private final ObjectMapper mapper = ObjectMapperFactory.getInstance();

    public Json(List<String> strings) {
        super(strings);
    }

    public Json(String text) {
        super(text);
    }

    /**
     * remove padding for JSONP
     * @param padding padding
     * @return json after padding removed
     */
    public Json removePadding(String padding) {
        String text = getFirstSourceText();
        XTokenQueue tokenQueue = new XTokenQueue(text);
        tokenQueue.consumeWhitespace();
        tokenQueue.consume(padding);
        tokenQueue.consumeWhitespace();
        String chompBalanced = tokenQueue.chompBalancedNotInQuotes('(', ')');
        return new Json(chompBalanced);
    }

    public <T> T toObject(Class<T> clazz) {
        if (getFirstSourceText() == null) {
            return null;
        }
        return mapper.convertValue(getFirstSourceText(), clazz);
    }

    public <T> List<T> toList() {
        if (getFirstSourceText() == null) {
            return null;
        }
        return mapper.convertValue(getFirstSourceText(), new TypeReference<>() {
        });
    }

    @Override
    public Selectable jsonPath(String jsonPath) {
        JsonPathSelector jsonPathSelector = new JsonPathSelector(jsonPath);
        return selectList(jsonPathSelector,getSourceTexts());
    }
}
