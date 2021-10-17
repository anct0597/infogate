package vn.infogate.web.pipeline;

import vn.infogate.json.JsonModelGatherPipeline;
import vn.infogate.web.collectors.CenHomeCollectors;

/**
 * @author anct.
 */
public class CenHomeModelGatherPipeline extends JsonModelGatherPipeline {

    public CenHomeModelGatherPipeline() {
        super(CenHomeCollectors.values());
    }
}
