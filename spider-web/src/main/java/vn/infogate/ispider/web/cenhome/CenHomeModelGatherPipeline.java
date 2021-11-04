package vn.infogate.ispider.web.cenhome;

import vn.infogate.ispider.json.JsonModelGatherPipeline;

/**
 * @author anct.
 */
public class CenHomeModelGatherPipeline extends JsonModelGatherPipeline {

    public CenHomeModelGatherPipeline() {
        super(CenHomeCollectors.values());
    }
}
