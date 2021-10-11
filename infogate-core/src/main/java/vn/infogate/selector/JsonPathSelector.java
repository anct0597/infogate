package vn.infogate.selector;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.collections4.CollectionUtils;
import vn.infogate.utils.JsonUtils;
import vn.infogate.utils.ObjectMapperFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * JsonPath selector.<br>
 * Used to extract content from JSON.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.1
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class JsonPathSelector implements Selector {

    private final JsonPath jsonPath;
    private final ObjectMapper mapper;

    public JsonPathSelector(String jsonPathStr) {
        this.jsonPath = JsonPath.compile(jsonPathStr);
        this.mapper = ObjectMapperFactory.getInstance();
    }

    @Override
    public String select(String text) {
        var object = jsonPath.read(text);
        if (object == null) return null;
        if (object instanceof List) {
            List list = (List) object;
            if (CollectionUtils.isNotEmpty(list)) {
                return toString(list.iterator().next());
            }
        }
        return object.toString();
    }

    private String toString(Object object) {
        return object instanceof Map ? JsonUtils.toJson(mapper, object) : object.toString();
    }

    @Override
    public List<String> selectList(String text) {
        Object object = jsonPath.read(text);
        if (object == null) return Collections.emptyList();

        List<String> list = new ArrayList<>();
        if (object instanceof List) {
            List<Object> items = (List<Object>) object;
            for (Object item : items) {
                list.add(toString(item));
            }
        } else {
            list.add(toString(object));
        }
        return list;
    }
}
