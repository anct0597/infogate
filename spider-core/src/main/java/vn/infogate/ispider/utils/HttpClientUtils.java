package vn.infogate.ispider.utils;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class HttpClientUtils {

    public static Map<String, List<String>> convertHeaders(Header[] headers) {
        var results = new HashMap<String, List<String>>();
        for (var header : headers) {
            var list = results.computeIfAbsent(header.getName(), k -> new ArrayList<>());
            list.add(header.getValue());
        }
        return results;
    }
}
