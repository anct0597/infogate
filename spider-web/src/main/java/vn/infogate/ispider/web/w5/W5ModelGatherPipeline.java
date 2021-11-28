package vn.infogate.ispider.web.w5;

import vn.infogate.ispider.extension.json.JsonModelGatherPipeline;

/**
 * @author anct.
 */
public class W5ModelGatherPipeline extends JsonModelGatherPipeline {

    public W5ModelGatherPipeline() {
        super(W5Collectors.values());
    }
}
