package vn.infogate.web.pipeline;

import vn.infogate.Task;
import vn.infogate.pipeline.PageModelPipeline;
import vn.infogate.web.model.CenHome;

/**
 * @author anct
 */
public class CenHomeCollectorPipeline implements PageModelPipeline<CenHome> {

    @Override
    public void process(CenHome cenHome, Task task) {
        System.out.println(cenHome);
    }
}
