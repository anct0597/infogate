package vn.infogate.scheduler;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import vn.infogate.Request;
import vn.infogate.Task;
import vn.infogate.scheduler.component.DuplicateRemover;
import vn.infogate.scheduler.component.HashSetDuplicateRemover;
import vn.infogate.utils.HttpConstant;

/**
 * Remove duplicate urls and only push urls which are not duplicate.<br><br>
 *
 * @author code4crafer@gmail.com
 * @since 0.5.0
 */
@Slf4j
@Getter
@Setter
public abstract class DuplicateRemovedScheduler implements Scheduler {

    private DuplicateRemover duplicateRemover = new HashSetDuplicateRemover();

    @Override
    public void push(Request request, Task task) {
        log.trace("get a candidate url {}", request.getUrl());
        if (shouldReserved(request) || noNeedToRemoveDuplicate(request) || !duplicateRemover.isDuplicate(request, task)) {
            log.debug("push to queue {}", request.getUrl());
            pushWhenNoDuplicate(request, task);
        }
    }

    protected boolean shouldReserved(Request request) {
        return request.getExtra(Request.CYCLE_TRIED_TIMES) != null;
    }

    protected boolean noNeedToRemoveDuplicate(Request request) {
        return HttpConstant.Method.POST.equalsIgnoreCase(request.getMethod());
    }

    protected void pushWhenNoDuplicate(Request request, Task task) {
    }
}
