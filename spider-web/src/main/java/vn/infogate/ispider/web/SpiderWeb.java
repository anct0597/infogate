package vn.infogate.ispider.web;

import lombok.extern.slf4j.Slf4j;
import vn.infogate.ispider.common.objectmapper.ObjectMapperFactory;
import vn.infogate.ispider.core.Spider;
import vn.infogate.ispider.core.downloader.Downloader;
import vn.infogate.ispider.core.downloader.HttpClientDownloader;
import vn.infogate.ispider.core.downloader.PhantomJSDownloader;
import vn.infogate.ispider.core.pipeline.Pipeline;
import vn.infogate.ispider.extension.json.JsonDefinedPageProcessor;
import vn.infogate.ispider.extension.json.JsonPageModel;
import vn.infogate.ispider.extension.json.JsonSpiderConfig;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SpiderWeb {

    private static final String DEFAULT_DEPLOY_FOLDER = "spider-web/deploy";

    private static JsonDefinedPageProcessor newPageProcessorInstance(JsonPageModel pageModel) throws Exception {
        //Init pageProcessor
        var pageProcessorClass = Class.forName(pageModel.getPageProcessor());
        var pageProcess = (JsonDefinedPageProcessor) pageProcessorClass.getDeclaredConstructor().newInstance();
        pageProcess.init(pageModel);
        return pageProcess;
    }

    private static List<Pipeline> createPipelines(JsonPageModel pageModel) throws Exception {
        //Init pipeline
        if (pageModel.getPipelines().isEmpty()) {
            throw new IllegalArgumentException("Missing pipeline config");
        }

        var pipelines = new ArrayList<Pipeline>(pageModel.getPipelines().size());
        for (var className : pageModel.getPipelines()) {
            var pipelineClass = Class.forName(className);
            pipelines.add((Pipeline) pipelineClass.getDeclaredConstructor().newInstance());
        }
        return pipelines;
    }

    private static Downloader createDownloader(JsonSpiderConfig config) {
        switch (config.getDownloader()) {
            case SELENIUM:
                return null;
            case PHANTOMJS:
                return new PhantomJSDownloader(config.getPhantomJs());
            default:
                return new HttpClientDownloader();
        }
    }

    public static void main(String[] args) throws Exception {
        var mapper = ObjectMapperFactory.getInstance();

        Files.walk(Path.of(DEFAULT_DEPLOY_FOLDER)).filter(path -> path.toFile().isFile() && path.toString().endsWith(".json")).forEach(file -> {
            try {
                var value = Files.readString(file.toAbsolutePath());
                var spiderConfig = mapper.readValue(value, JsonSpiderConfig.class);

                if (spiderConfig.getStatus() == 1) {
                    var pageModel = spiderConfig.getPageModel();
                    var pageProcessor = newPageProcessorInstance(pageModel);
                    var pipelines = createPipelines(pageModel);
                    Spider.create(pageProcessor)
                            .setDownloader(createDownloader(spiderConfig))
                            .setPipelines(pipelines)
                            .addUrl(pageModel.getStartUrls())
                            .thread(spiderConfig.getThread())
                            .run();
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }
}
