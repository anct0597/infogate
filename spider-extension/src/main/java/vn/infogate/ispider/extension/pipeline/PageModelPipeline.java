package vn.infogate.ispider.extension.pipeline;

import vn.infogate.ispider.core.Task;

/**
 * Implements PageModelPipeline to persistent your page model.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
public interface PageModelPipeline<T> {

    void process(T t, Task task);
}
