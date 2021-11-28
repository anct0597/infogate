package vn.infogate.ispider.extension.handler;

import vn.infogate.ispider.core.ResultItems;
import vn.infogate.ispider.core.Task;

/**
 * @author code4crafer@gmail.com
 * @since 0.5.0
 */
public interface SubPipeline extends RequestMatcher {

    /**
     * process the page, extract urls to fetch, extract the data and store
     *
     * @param resultItems resultItems
     * @param task        task
     * @return whether continue to match
     */
    MatchOther processResult(ResultItems resultItems, Task task);

}
