package vn.infogate.ispider.web.w2;

import vn.infogate.ispider.json.JsonModelGatherPipeline;

/**
 * @author anct.
 */
public class W2ModelGatherPipeline extends JsonModelGatherPipeline {

    public W2ModelGatherPipeline() {
        super(W2Collectors.values());
    }
}
