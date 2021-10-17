package vn.infogate.web.processors;

import lombok.extern.slf4j.Slf4j;
import vn.infogate.Page;
import vn.infogate.Site;
import vn.infogate.json.JsonDefinedPageProcessor;
import vn.infogate.json.JsonPageProcessor;

@Slf4j
@JsonPageProcessor
public class CenHomePageProcessor extends JsonDefinedPageProcessor {

    @Override
    public Site getSite() {
        return Site.me().setSleepTime(1000);
    }

    @Override
    protected void postProcess(Page page) {
        System.out.println(page.getResultItems());
    }
}
