package vn.infogate.ispider.web.pipeline;

import vn.infogate.ispider.web.collectors.CenHomeCollectors;
import vn.infogate.ispider.json.JsonModelGatherPipeline;

/**
 * @author anct.
 */
public class CenHomeModelGatherPipeline extends JsonModelGatherPipeline {

    public CenHomeModelGatherPipeline() {
        super(CenHomeCollectors.values());
    }
}
