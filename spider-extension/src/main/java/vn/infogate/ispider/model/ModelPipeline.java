package vn.infogate.ispider.model;

import vn.infogate.ispider.ResultItems;
import vn.infogate.ispider.Task;
import vn.infogate.ispider.model.annotation.ExtractBy;
import vn.infogate.ispider.pipeline.PageModelPipeline;
import vn.infogate.ispider.pipeline.Pipeline;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The extension to Pipeline for page model extractor.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class ModelPipeline implements Pipeline {

    private final Map<Class<?>, PageModelPipeline> pageModelPipelines;

    public ModelPipeline() {
        this.pageModelPipelines = new ConcurrentHashMap<>(1);
    }

    public void put(Class<?> clazz, PageModelPipeline pageModelPipeline) {
        pageModelPipelines.put(clazz, pageModelPipeline);
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        for (var classPageModelPipelineEntry : pageModelPipelines.entrySet()) {
            Object o = resultItems.get(classPageModelPipelineEntry.getKey().getCanonicalName());
            if (o != null) {
                Annotation annotation = classPageModelPipelineEntry.getKey().getAnnotation(ExtractBy.class);
                if (annotation == null) { //TODO ??
                    classPageModelPipelineEntry.getValue().process(o, task);
                } else {
                    List<Object> list = (List<Object>) o;
                    for (Object obj : list) {
                        classPageModelPipelineEntry.getValue().process(obj, task);
                    }
                }
            }
        }
    }
}
