package vn.infogate.ispider.pipeline;

import vn.infogate.ispider.ResultItems;
import vn.infogate.ispider.Task;

/**
 * Pipeline is the persistent and offline process part of crawler.<br>
 * The interface Pipeline can be implemented to customize ways of persistent.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 * @see FilePipeline
 */
public interface Pipeline {

    /**
     * Process extracted results.
     *
     * @param resultItems resultItems
     * @param task task
     */
    void process(ResultItems resultItems, Task task);
}
