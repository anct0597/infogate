package vn.infogate.pipeline;

import java.util.List;

/**
 * Pipeline that can collect and store results. <br>
 *
 * @author code4crafter@gmail.com
 * @since 0.4.0
 */
public interface CollectorPipeline<T> extends Pipeline {

    /**
     * Get all results collected.
     *
     * @return collected results
     */
    List<T> getCollected();
}
