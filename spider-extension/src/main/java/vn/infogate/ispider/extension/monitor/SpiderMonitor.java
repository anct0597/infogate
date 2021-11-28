package vn.infogate.ispider.extension.monitor;

import lombok.extern.slf4j.Slf4j;
import vn.infogate.ispider.core.Request;
import vn.infogate.ispider.core.Spider;
import vn.infogate.ispider.core.SpiderListener;
import vn.infogate.ispider.core.utils.Experimental;
import vn.infogate.ispider.core.utils.UrlUtils;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author code4crafer@gmail.com
 * @since 0.5.0
 */
@Slf4j
@Experimental
public class SpiderMonitor {

    private static final SpiderMonitor INSTANCE = new SpiderMonitor();
    private final MBeanServer mbeanServer;
    private final String jmxServerName;
    private final List<SpiderStatusMXBean> spiderStatuses = new ArrayList<>();

    protected SpiderMonitor() {
        jmxServerName = "WebMagic";
        mbeanServer = ManagementFactory.getPlatformMBeanServer();
    }

    /**
     * Register spider for monitor.
     *
     * @param spiders spiders
     * @return this
     * @throws JMException JMException
     */
    public synchronized SpiderMonitor register(Spider... spiders) throws JMException {
        for (Spider spider : spiders) {
            MonitorSpiderListener monitorSpiderListener = new MonitorSpiderListener();
            if (spider.getSpiderListeners() == null) {
                List<SpiderListener> spiderListeners = new ArrayList<>();
                spiderListeners.add(monitorSpiderListener);
                spider.setSpiderListeners(spiderListeners);
            } else {
                spider.getSpiderListeners().add(monitorSpiderListener);
            }
            SpiderStatusMXBean spiderStatusMBean = getSpiderStatusMBean(spider, monitorSpiderListener);
            registerMBean(spiderStatusMBean);
            spiderStatuses.add(spiderStatusMBean);
        }
        return this;
    }

    protected SpiderStatusMXBean getSpiderStatusMBean(Spider spider, MonitorSpiderListener monitorSpiderListener) {
        return new SpiderStatus(spider, monitorSpiderListener);
    }

    protected List<SpiderStatusMXBean> getSpiderStatuses() {
        return this.spiderStatuses;
    }

    public static SpiderMonitor instance() {
        return INSTANCE;
    }

    public static class MonitorSpiderListener implements SpiderListener {

        private final AtomicInteger successCount = new AtomicInteger(0);

        private final AtomicInteger errorCount = new AtomicInteger(0);

        private final List<String> errorUrls = Collections.synchronizedList(new ArrayList<>());

        @Override
        public void onSuccess(Request request) {
            successCount.incrementAndGet();
        }

        @Override
        public void onError(Request request, Exception e) {
            errorUrls.add(request.getUrl());
            errorCount.incrementAndGet();
        }

        public AtomicInteger getSuccessCount() {
            return successCount;
        }

        public AtomicInteger getErrorCount() {
            return errorCount;
        }

        public List<String> getErrorUrls() {
            return errorUrls;
        }
    }

    protected void registerMBean(SpiderStatusMXBean spiderStatus) throws JMException {
//        ObjectName objName = new ObjectName(jmxServerName + ":name=" + spiderStatus.getName());
        var objName = new ObjectName(jmxServerName + ":name=" + UrlUtils.removePort(spiderStatus.getName()));
        mbeanServer.registerMBean(spiderStatus, objName);
    }

}
