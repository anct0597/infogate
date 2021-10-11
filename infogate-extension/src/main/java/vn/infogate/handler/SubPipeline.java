package vn.infogate.handler;

import vn.infogate.ResultItems;
import vn.infogate.Task;

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
