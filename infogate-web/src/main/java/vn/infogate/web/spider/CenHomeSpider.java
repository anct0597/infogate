package vn.infogate.web.spider;

import vn.infogate.Site;
import vn.infogate.model.OOSpider;
import vn.infogate.web.model.CenHome;
import vn.infogate.web.pipeline.CenHomeCollectorPipeline;

/**
 * @author anct
 */
public class CenHomeSpider {

    public static void main(String[] args) {
        OOSpider.create(Site.me().setSleepTime(1000), new CenHomeCollectorPipeline(), CenHome.class)
                .addUrl("https://cenhomes.vn/du-an/chung-cu-binh-minh-garden-10000373").thread(1).run();
    }
}
