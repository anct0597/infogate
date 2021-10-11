package vn.infogate.model;

import lombok.Getter;
import lombok.Setter;
import vn.infogate.Page;
import vn.infogate.Request;
import vn.infogate.Site;
import vn.infogate.processor.PageProcessor;
import vn.infogate.selector.Selector;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The extension to PageProcessor for page model extractor.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
@Getter
@Setter
public class ModelPageProcessor implements PageProcessor {

    private final Site site;
    private boolean extractLinks = true;
    private final List<PageModelExtractor> pageModelExtractorList = new ArrayList<>();

    public static ModelPageProcessor create(Site site, Class<?>... classLst) {
        var modelPageProcessor = new ModelPageProcessor(site);
        for (Class<?> clazz : classLst) {
            modelPageProcessor.addPageModel(clazz);
        }
        return modelPageProcessor;
    }


    public ModelPageProcessor addPageModel(Class<?> clazz) {
        var pageModelExtractor = PageModelExtractor.create(clazz);
        pageModelExtractorList.add(pageModelExtractor);
        return this;
    }

    private ModelPageProcessor(Site site) {
        this.site = site;
    }

    @Override
    public void process(Page page) {
        for (var pageModelExtractor : pageModelExtractorList) {
            if (extractLinks) {
                extractLinks(page, pageModelExtractor.getHelpUrlRegionSelector(), pageModelExtractor.getHelpUrlPatterns());
                extractLinks(page, pageModelExtractor.getTargetUrlRegionSelector(), pageModelExtractor.getTargetUrlPatterns());
            }
            Object process = pageModelExtractor.process(page);
            if (process == null || (process instanceof List && ((List) process).size() == 0)) {
                continue;
            }
            postProcessPageModel(pageModelExtractor.getClazz(), process);
            page.putField(pageModelExtractor.getClazz().getCanonicalName(), process);
         }
        if (page.getResultItems().getAll().size() == 0) {
            page.getResultItems().setSkip(true);
        }
    }

    private void extractLinks(Page page, Selector urlRegionSelector, List<Pattern> urlPatterns) {
        List<String> links;
        if (urlRegionSelector == null) {
            links = page.getHtml().links().all();
        } else {
            links = page.getHtml().selectList(urlRegionSelector).links().all();
        }
        for (String link : links) {
            for (Pattern targetUrlPattern : urlPatterns) {
                Matcher matcher = targetUrlPattern.matcher(link);
                if (matcher.find()) {
                    page.addTargetRequest(new Request(matcher.group(0)));
                }
            }
        }
    }

    protected void postProcessPageModel(Class<?> clazz, Object object) {
        // do thing?
    }
}
