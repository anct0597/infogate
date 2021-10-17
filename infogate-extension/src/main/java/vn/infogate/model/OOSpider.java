package vn.infogate.model;

import vn.infogate.Site;
import vn.infogate.Spider;
import vn.infogate.pipeline.PageModelPipeline;

/**
 * @author anct.
 */
@SuppressWarnings({"rawtypes"})
public class OOSpider extends Spider {

    private ModelPipeline modelPipeline;
    private final ModelPageProcessor modelPageProcessor;

    private OOSpider(ModelPageProcessor modelPageProcessor) {
        super(modelPageProcessor);
        this.modelPageProcessor = modelPageProcessor;
    }

    /**
     * create a spider
     *
     * @param site              site
     * @param pageModelPipeline pageModelPipeline
     * @param pageModels        pageModels
     */
    public OOSpider(Site site, PageModelPipeline pageModelPipeline, Class<?>... pageModels) {
        this(ModelPageProcessor.create(site, pageModels));
        this.modelPipeline = new ModelPipeline();
        super.addPipeline(modelPipeline);
        for (var pageModel : pageModels) {
            if (pageModelPipeline != null) {
                this.modelPipeline.put(pageModel, pageModelPipeline);
            }
        }
    }

    public static OOSpider create(Site site, Class<?>... pageModels) {
        return new OOSpider(site, null, pageModels);
    }

    public static OOSpider create(Site site, PageModelPipeline pageModelPipeline, Class... pageModels) {
        return new OOSpider(site, pageModelPipeline, pageModels);
    }

    public OOSpider addPageModel(PageModelPipeline pageModelPipeline, Class<?>... pageModels) {
        for (Class pageModel : pageModels) {
            modelPageProcessor.addPageModel(pageModel);
            modelPipeline.put(pageModel, pageModelPipeline);
        }
        return this;
    }
}
