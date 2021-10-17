package vn.infogate.json;

import org.apache.commons.collections4.CollectionUtils;
import vn.infogate.ResultItems;
import vn.infogate.Task;
import vn.infogate.common.utils.Utils;
import vn.infogate.pipeline.Pipeline;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author anct.
 */
public abstract class JsonModelGatherPipeline implements Pipeline {

    private final List<JsonFieldCollector> collectors;

    public JsonModelGatherPipeline(JsonFieldCollector[] collectors) {
        this.collectors = Arrays.asList(collectors);
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        if (CollectionUtils.isEmpty(collectors) || resultItems.isSkip()) return;
        var fields = new HashMap<String, Object>();
        for (var collector : collectors) {
            var rawValue = resultItems.get(collector.extractFrom());
            if (Utils.isNullOrEmpty(rawValue)) continue;
            fields.put(collector.fieldName(), collector.collect(rawValue));
        }
        resultItems.initAll(fields);
    }
}
