package vn.infogate.ispider.web.w3;

import lombok.extern.slf4j.Slf4j;
import vn.infogate.ispider.core.Page;
import vn.infogate.ispider.core.Site;
import vn.infogate.ispider.extension.json.JsonDefinedPageProcessor;
import vn.infogate.ispider.extension.json.JsonPageProcessor;

@Slf4j
@JsonPageProcessor
public class W3PageProcessor extends JsonDefinedPageProcessor {

    @Override
    public Site getSite() {
        return Site.me().setSleepTime(1000);
    }

    @Override
    protected void postProcess(Page page) {
        // Do anything here
    }
}
