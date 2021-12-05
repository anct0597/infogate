package vn.infogate.ispider.extension.model;

import lombok.Getter;
import lombok.Setter;
import vn.infogate.ispider.common.utils.Utils;
import vn.infogate.ispider.core.Page;
import vn.infogate.ispider.core.Request;
import vn.infogate.ispider.core.Site;
import vn.infogate.ispider.core.processor.PageProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * The extension to PageProcessor for page model extractor.
 *
 * @author anct.
 * @version 1.0
 */
@Getter
@Setter
public class ModelPageProcessor implements PageProcessor {

    private final Site site;
    private final List<PageModelExtractor> pageModelExtractorList;

    private ModelPageProcessor(Site site) {
        this.site = site;
        this.pageModelExtractorList = new ArrayList<>(1);
    }

    public static ModelPageProcessor create(Site site, Class<?>... classLst) {
        var modelPageProcessor = new ModelPageProcessor(site);
        for (Class<?> clazz : classLst) {
            modelPageProcessor.addPageModel(clazz);
        }
        return modelPageProcessor;
    }

    public void addPageModel(Class<?> clazz) {
        var pageModelExtractor = PageModelExtractor.create(clazz);
        pageModelExtractorList.add(pageModelExtractor);
    }

    @Override
    public void process(Page page) {
        for (var pageModelExtractor : pageModelExtractorList) {
            extractLinks(page, pageModelExtractor);
            Object modelObj = pageModelExtractor.process(page);
            if (Utils.isNullOrEmpty(modelObj)) continue;

            postProcessPageModel(pageModelExtractor.getClazz(), modelObj);
            page.putField(pageModelExtractor.getClazz().getCanonicalName(), modelObj);
        }
        if (page.getResultItems().isEmpty()) {
            page.getResultItems().setSkip(true);
        }
    }

    private void extractLinks(Page page, PageModelExtractor extractor) {
        List<String> links;
        var regionSelector = extractor.getTargetUrlRegionSelector();
        if (regionSelector == null) {
            links = page.getHtml().links().all();
        } else {
            links = page.getHtml().selectList(regionSelector).links().all();
        }
        for (String link : links) {
            var ignored = false;
            for (var ignoredPattern : extractor.getIgnoredPatterns()) {
                var matcher = ignoredPattern.matcher(link);
                if (matcher.matches()) {
                    ignored = true;
                    break;
                }
            }
            if (ignored) continue;
            for (var urlPattern : extractor.getTargetUrlPatterns()) {
                var matcher = urlPattern.matcher(link);
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
