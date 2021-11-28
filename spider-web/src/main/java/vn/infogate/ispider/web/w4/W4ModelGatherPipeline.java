package vn.infogate.ispider.web.w4;

import vn.infogate.ispider.extension.json.JsonModelGatherPipeline;

/**
 * @author anct.
 */
public class W4ModelGatherPipeline extends JsonModelGatherPipeline {

    public W4ModelGatherPipeline() {
        super(W4Collectors.values());
    }
}
