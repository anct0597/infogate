package vn.infogate.ispider.web.w1;

import vn.infogate.ispider.extension.json.JsonModelGatherPipeline;

/**
 * @author anct.
 */
public class W1ModelGatherPipeline extends JsonModelGatherPipeline {

    public W1ModelGatherPipeline() {
        super(W1Collectors.values());
    }
}
