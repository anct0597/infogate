package vn.infogate.ispider.web.w3;

import vn.infogate.ispider.extension.json.JsonModelGatherPipeline;

/**
 * @author anct.
 */
public class W3ModelGatherPipeline extends JsonModelGatherPipeline {

    public W3ModelGatherPipeline() {
        super(W3Collectors.values());
    }
}
