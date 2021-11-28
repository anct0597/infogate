package vn.infogate.ispider.core.scheduler;

import vn.infogate.ispider.core.Request;
import vn.infogate.ispider.core.Task;

import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Priority scheduler. Request with higher priority will poll earlier. <br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.1
 */
public class PriorityScheduler extends DuplicateRemovedScheduler implements MonitoredScheduler {

    public static final int INITIAL_CAPACITY = 5;

    private final BlockingQueue<Request> noPriorityQueue = new LinkedBlockingQueue<>();

    private final PriorityBlockingQueue<Request> priorityQueuePlus =
            new PriorityBlockingQueue<>(INITIAL_CAPACITY, Comparator.comparingLong(Request::getPriority));

    private final PriorityBlockingQueue<Request> priorityQueueMinus =
            new PriorityBlockingQueue<>(INITIAL_CAPACITY, Comparator.comparingLong(Request::getPriority));

    @Override
    public void pushWhenNoDuplicate(Request request, Task task) {
        if (request.getPriority() == 0) {
            noPriorityQueue.add(request);
        } else if (request.getPriority() > 0) {
            priorityQueuePlus.put(request);
        } else {
            priorityQueueMinus.put(request);
        }
    }

    @Override
    public synchronized Request poll(Task task) {
        Request poll = priorityQueuePlus.poll();
        if (poll != null) {
            return poll;
        }
        poll = noPriorityQueue.poll();
        if (poll != null) {
            return poll;
        }
        return priorityQueueMinus.poll();
    }

    @Override
    public int getLeftRequestsCount(Task task) {
        return noPriorityQueue.size();
    }

    @Override
    public int getTotalRequestsCount(Task task) {
        return getDuplicateRemover().getTotalRequestsCount(task);
    }
}
