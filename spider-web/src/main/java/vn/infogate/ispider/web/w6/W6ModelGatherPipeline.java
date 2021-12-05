package vn.infogate.ispider.web.w6;

import vn.infogate.ispider.extension.json.JsonModelGatherPipeline;

/**
 * @author anct.
 */
public class W6ModelGatherPipeline extends JsonModelGatherPipeline {

    public W6ModelGatherPipeline() {
        super(W6Collectors.values());
    }
}
