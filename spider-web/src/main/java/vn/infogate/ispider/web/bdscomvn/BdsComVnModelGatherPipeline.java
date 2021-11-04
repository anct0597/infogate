package vn.infogate.ispider.web.bdscomvn;

import vn.infogate.ispider.json.JsonModelGatherPipeline;

/**
 * @author anct.
 */
public class BdsComVnModelGatherPipeline extends JsonModelGatherPipeline {

    public BdsComVnModelGatherPipeline() {
        super(BdsComVnCollectors.values());
    }
}
