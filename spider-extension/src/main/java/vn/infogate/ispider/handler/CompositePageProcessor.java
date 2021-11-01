package vn.infogate.ispider.handler;

import vn.infogate.ispider.Page;
import vn.infogate.ispider.Site;
import vn.infogate.ispider.processor.PageProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author code4crafter@gmail.com
 */
public class CompositePageProcessor implements PageProcessor {

    private Site site;

    private final List<SubPageProcessor> subPageProcessors = new ArrayList<>();

    public CompositePageProcessor(Site site) {
        this.site = site;
    }

    @Override
    public void process(Page page) {
        for (SubPageProcessor subPageProcessor : subPageProcessors) {
            if (subPageProcessor.match(page.getRequest())) {
                SubPageProcessor.MatchOther matchOtherProcessorProcessor = subPageProcessor.processPage(page);
                if (matchOtherProcessorProcessor != SubPageProcessor.MatchOther.YES) {
                    return;
                }
            }
        }
    }

    public CompositePageProcessor setSite(Site site) {
        this.site = site;
        return this;
    }

    public CompositePageProcessor addSubPageProcessor(SubPageProcessor subPageProcessor) {
        this.subPageProcessors.add(subPageProcessor);
        return this;
    }

    public CompositePageProcessor setSubPageProcessors(SubPageProcessor... subPageProcessors) {
        this.subPageProcessors.clear();
        this.subPageProcessors.addAll(Arrays.asList(subPageProcessors));
        return this;
    }

    @Override
    public Site getSite() {
        return site;
    }
}
