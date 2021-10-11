package vn.infogate.scheduler;

import vn.infogate.Task;

/**
 * The scheduler whose requests can be counted for monitor.
 *
 * @author code4crafter@gmail.com
 * @since 0.5.0
 */
public interface MonitoredScheduler extends Scheduler {

    int getLeftRequestsCount(Task task);

    int getTotalRequestsCount(Task task);

}