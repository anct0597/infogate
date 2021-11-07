package vn.infogate.ispider.web.w4;

import lombok.extern.slf4j.Slf4j;
import vn.infogate.ispider.Page;
import vn.infogate.ispider.Site;
import vn.infogate.ispider.json.JsonDefinedPageProcessor;
import vn.infogate.ispider.json.JsonPageProcessor;

@Slf4j
@JsonPageProcessor
public class W4PageProcessor extends JsonDefinedPageProcessor {

    @Override
    public Site getSite() {
        return Site.me().setSleepTime(1000);
    }

    @Override
    protected void postProcess(Page page) {
        // Do anything here
    }
}