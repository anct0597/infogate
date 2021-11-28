package vn.infogate.ispider.core.selector;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.apache.commons.collections4.CollectionUtils;
import vn.infogate.ispider.common.objectmapper.ObjectMapperFactory;
import vn.infogate.ispider.core.utils.JsonUtils;

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
        var obj = jsonPath.read(text);
        if (obj == null) return Collections.emptyList();

        var list = new ArrayList<String>(5);
        if (obj instanceof JSONArray) {
            for (Object item : (JSONArray) obj) {
                if (item instanceof List) {
                    for (var el : (List<Object>) item) {
                        list.add(toString(el));
                    }
                } else {
                    list.add(toString(item));
                }
            }
        } else {
            list.add(toString(obj));
        }
        return list;
    }
}
