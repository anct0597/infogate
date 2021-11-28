package vn.infogate.ispider.core;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import vn.infogate.ispider.core.downloader.Downloader;
import vn.infogate.ispider.core.downloader.HttpClientDownloader;
import vn.infogate.ispider.core.pipeline.CollectorPipeline;
import vn.infogate.ispider.core.pipeline.Pipeline;
import vn.infogate.ispider.core.pipeline.ResultItemsCollectorPipeline;
import vn.infogate.ispider.core.processor.PageProcessor;
import vn.infogate.ispider.core.scheduler.QueueScheduler;
import vn.infogate.ispider.core.scheduler.Scheduler;
import vn.infogate.ispider.core.thread.CountableThreadPool;
import vn.infogate.ispider.core.utils.UrlUtils;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author anct
 */
@Slf4j
@SuppressWarnings({"unchecked", "rawtypes"})
public class Spider implements Runnable, Task {

    protected Downloader downloader;
    protected PageProcessor pageProcessor;
    protected List<Request> startRequests;

    @Getter
    protected Scheduler scheduler = new QueueScheduler();
    protected CountableThreadPool threadPool;
    protected ExecutorService executorService;
    protected int threadNum = 1;
    protected boolean exitWhenComplete = true;
    protected final static int STATUS_INIT = 0;
    protected final static int STATUS_RUNNING = 1;
    protected final static int STATUS_STOPPED = 2;
    protected boolean spawnUrl = true;
    protected boolean destroyWhenExit = true;

    protected AtomicInteger status = new AtomicInteger(STATUS_INIT);
    private final AtomicLong pageCount = new AtomicLong(0);
    private long emptySleepTime = 30000;

    private final ReentrantLock newUrlLock = new ReentrantLock();
    private final Condition newUrlCondition = newUrlLock.newCondition();

    @Getter
    private List<SpiderListener> spiderListeners;
    protected List<Pipeline> pipelines = new ArrayList<>();

    protected Site site;
    protected String uuid;

    @Getter
    private Date startTime;

    /**
     * create a spider with pageProcessor.
     *
     * @param pageProcessor pageProcessor
     * @return new spider
     * @see PageProcessor
     */
    public static Spider create(PageProcessor pageProcessor) {
        return new Spider(pageProcessor);
    }

    /**
     * create a spider with pageProcessor.
     *
     * @param pageProcessor pageProcessor
     */
    public Spider(PageProcessor pageProcessor) {
        this.pageProcessor = pageProcessor;
        this.site = pageProcessor.getSite();
    }

    /**
     * Set startUrls of Spider.<br>
     * Prior to startUrls of Site.
     *
     * @param startUrls startUrls
     * @return this
     */
    public Spider startUrls(List<String> startUrls) {
        checkIfRunning();
        this.startRequests = UrlUtils.convertToRequests(startUrls);
        return this;
    }

    /**
     * Set startUrls of Spider.<br>
     * Prior to startUrls of Site.
     *
     * @param startRequests startRequests
     * @return this
     */
    public Spider startRequest(List<Request> startRequests) {
        checkIfRunning();
        this.startRequests = startRequests;
        return this;
    }

    /**
     * Set an uuid for spider.<br>
     * Default uuid is domain of site.<br>
     *
     * @param uuid uuid
     * @return this
     */
    public Spider setUUID(String uuid) {
        this.uuid = uuid;
        return this;
    }

    /**
     * set scheduler for Spider
     *
     * @param scheduler scheduler
     * @return this
     * @see Scheduler
     * @since 0.2.1
     */
    public Spider setScheduler(Scheduler scheduler) {
        checkIfRunning();
        Scheduler oldScheduler = this.scheduler;
        this.scheduler = scheduler;
        if (oldScheduler != null) {
            Request request;
            while ((request = oldScheduler.poll(this)) != null) {
                this.scheduler.push(request, this);
            }
        }
        return this;
    }

    /**
     * add a pipeline for Spider
     *
     * @param pipeline pipeline
     * @return this
     * @see Pipeline
     * @since 0.2.1
     */
    public Spider addPipeline(Pipeline pipeline) {
        checkIfRunning();
        this.pipelines.add(pipeline);
        return this;
    }

    /**
     * set pipelines for Spider
     *
     * @param pipelines pipelines
     * @return this
     * @see Pipeline
     * @since 0.4.1
     */
    public Spider setPipelines(List<Pipeline> pipelines) {
        checkIfRunning();
        this.pipelines = pipelines;
        return this;
    }

    /**
     * clear the pipelines set
     *
     * @return this
     */
    public Spider clearPipeline() {
        pipelines.clear();
        return this;
    }

    /**
     * set the downloader of spider
     *
     * @param downloader downloader
     * @return this
     * @see Downloader
     */
    public Spider setDownloader(Downloader downloader) {
        checkIfRunning();
        this.downloader = downloader;
        return this;
    }

    protected void initComponent() {
        if (downloader == null) {
            this.downloader = new HttpClientDownloader();
        }
        downloader.setThread(threadNum);
        if (threadPool == null || threadPool.isShutdown()) {
            if (executorService != null && !executorService.isShutdown()) {
                threadPool = new CountableThreadPool(threadNum, executorService);
            } else {
                threadPool = new CountableThreadPool(threadNum);
            }
        }
        if (startRequests != null) {
            for (Request request : startRequests) {
                addRequest(request);
            }
            startRequests.clear();
        }
        startTime = new Date();
    }

    @Override
    public void run() {
        checkRunningStatus();
        initComponent();
        log.info("Spider {} started!", getUUID());
        // interrupt won't be necessarily detected
        while (!Thread.currentThread().isInterrupted() && status.get() == STATUS_RUNNING) {
            Request poll = scheduler.poll(this);
            if (poll == null) {
                if (threadPool.getThreadAlive() == 0) {
                    //no alive thread anymore , try again
                    poll = scheduler.poll(this);
                    if (poll == null) {
                        if (exitWhenComplete) {
                            break;
                        } else {
                            // wait
                            try {
                                Thread.sleep(emptySleepTime);
                                continue;
                            } catch (InterruptedException e) {
                                break;
                            }
                        }
                    }
                } else {
                    // wait until new url added，
                    if (waitNewUrl())
                        //if interrupted
                        break;
                    continue;
                }
            }
            final Request request = poll;
            //this may swallow the interruption
            threadPool.execute(() -> {
                try {
                    processRequest(request);
                    onSuccess(request);
                } catch (Exception e) {
                    onError(request, e);
                    log.error("process request " + request + " error", e);
                } finally {
                    pageCount.incrementAndGet();
                    signalNewUrl();
                }
            });
        }
        status.set(STATUS_STOPPED);
        // release some resources
        if (destroyWhenExit) {
            close();
        }
        log.info("Spider {} closed! {} pages downloaded.", getUUID(), pageCount.get());
    }

    /**
     * @deprecated Use {@link #onError(Request, Exception)} instead.
     */
    @Deprecated
    protected void onError(Request request) {
    }

    protected void onError(Request request, Exception e) {
        this.onError(request);

        if (CollectionUtils.isNotEmpty(spiderListeners)) {
            for (SpiderListener spiderListener : spiderListeners) {
                spiderListener.onError(request, e);
            }
        }
    }

    protected void onSuccess(Request request) {
        if (CollectionUtils.isNotEmpty(spiderListeners)) {
            for (SpiderListener spiderListener : spiderListeners) {
                spiderListener.onSuccess(request);
            }
        }
    }

    private void checkRunningStatus() {
        while (true) {
            int statusNow = status.get();
            if (statusNow == STATUS_RUNNING) {
                throw new IllegalStateException("Spider is already running!");
            }
            if (status.compareAndSet(statusNow, STATUS_RUNNING)) {
                break;
            }
        }
    }

    public void close() {
        destroyEach(downloader);
        destroyEach(pageProcessor);
        destroyEach(scheduler);
        for (Pipeline pipeline : pipelines) {
            destroyEach(pipeline);
        }
        threadPool.shutdown();
    }

    private void destroyEach(Object object) {
        if (object instanceof Closeable) {
            try {
                ((Closeable) object).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processRequest(Request request) {
        Page page;
        if (null != request.getDownloader()) {
            page = request.getDownloader().download(request, this);
        } else {
            page = downloader.download(request, this);
        }
        if (page.isDownloadSuccess()) {
            onDownloadSuccess(request, page);
        } else {
            onDownloaderFail(request);
        }
    }

    private void onDownloadSuccess(Request request, Page page) {
        if (site.getAcceptStatusCode().contains(page.getStatusCode())) {
            pageProcessor.process(page);
            extractAndAddRequests(page, spawnUrl);
            if (!page.getResultItems().isSkip()) {
                for (Pipeline pipeline : pipelines) {
                    pipeline.process(page.getResultItems(), this);
                }
            }
        } else {
            log.info("page status code error, page {} , code: {}", request.getUrl(), page.getStatusCode());
        }
        sleep(site.getSleepTime());
    }

    private void onDownloaderFail(Request request) {
        if (site.getCycleRetryTimes() == 0) {
            sleep(site.getSleepTime());
        } else {
            // for cycle retry
            doCycleRetry(request);
        }
    }

    private void doCycleRetry(Request request) {
        Object cycleTriedTimesObject = request.getExtra(Request.CYCLE_TRIED_TIMES);
        if (cycleTriedTimesObject == null) {
            addRequest(SerializationUtils.clone(request).setPriority(0).putExtra(Request.CYCLE_TRIED_TIMES, 1));
        } else {
            int cycleTriedTimes = (Integer) cycleTriedTimesObject;
            cycleTriedTimes++;
            if (cycleTriedTimes < site.getCycleRetryTimes()) {
                addRequest(SerializationUtils.clone(request).setPriority(0).putExtra(Request.CYCLE_TRIED_TIMES, cycleTriedTimes));
            }
        }
        sleep(site.getRetrySleepTime());
    }

    protected void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            log.error("Thread interrupted when sleep", e);
        }
    }

    protected void extractAndAddRequests(Page page, boolean spawnUrl) {
        if (spawnUrl && CollectionUtils.isNotEmpty(page.getTargetRequests())) {
            for (Request request : page.getTargetRequests()) {
                addRequest(request);
            }
        }
    }

    private void addRequest(Request request) {
        if (site.getDomain() == null && request != null && request.getUrl() != null) {
            site.setDomain(UrlUtils.getDomain(request.getUrl()));
        }
        scheduler.push(request, this);
    }

    protected void checkIfRunning() {
        if (status.get() == STATUS_RUNNING) {
            throw new IllegalStateException("Spider is already running!");
        }
    }

    /**
     * Add urls to crawl. <br>
     *
     * @param urls urls
     * @return this
     */
    public Spider addUrl(List<String> urls) {
        for (String url : urls) {
            addRequest(new Request(url));
        }
        signalNewUrl();
        return this;
    }

    /**
     * Add urls to crawl. <br>
     *
     * @param urls urls
     * @return this
     */
    public Spider addUrl(String... urls) {
        for (String url : urls) {
            addRequest(new Request(url));
        }
        signalNewUrl();
        return this;
    }

    /**
     * Download urls synchronizing.
     *
     * @param urls urls
     * @param <T>  type of process result
     * @return list downloaded
     */
    public <T> List<T> getAll(String... urls) {
        destroyWhenExit = false;
        spawnUrl = false;
        if (startRequests != null) {
            startRequests.clear();
        }
        for (Request request : UrlUtils.convertToRequests(List.of(urls))) {
            addRequest(request);
        }
        var collectorPipeline = getCollectorPipeline();
        pipelines.add(collectorPipeline);
        run();
        spawnUrl = true;
        destroyWhenExit = true;
        return collectorPipeline.getCollected();
    }

    protected CollectorPipeline getCollectorPipeline() {
        return new ResultItemsCollectorPipeline();
    }

    /**
     * Get all result items of url.
     */
    public <T> T get(String url) {
        var resultItems = getAll(url);
        return CollectionUtils.isNotEmpty(resultItems) ? (T) resultItems.get(0) : null;
    }

    /**
     * @return isInterrupted
     */
    private boolean waitNewUrl() {
        // now there may not be any thread live
        newUrlLock.lock();
        try {
            //double check，unnecessary, unless very fast concurrent
            if (threadPool.getThreadAlive() == 0) {
                return false;
            }
            //wait for amount of time
            newUrlCondition.await(emptySleepTime, TimeUnit.MILLISECONDS);
            return false;
        } catch (InterruptedException e) {
            // logger.warn("waitNewUrl - interrupted, error {}", e);
            return true;
        } finally {
            newUrlLock.unlock();
        }
    }

    private void signalNewUrl() {
        try {
            newUrlLock.lock();
            newUrlCondition.signalAll();
        } finally {
            newUrlLock.unlock();
        }
    }

    /**
     * Start spider.
     */
    public void start() {
        var thread = new Thread(this);
        thread.setDaemon(false);
        thread.start();
    }

    /**
     * Stop spider.
     */
    public void stop() {
        if (status.compareAndSet(STATUS_RUNNING, STATUS_STOPPED)) {
            log.info("Spider " + getUUID() + " stop success!");
        } else {
            log.info("Spider " + getUUID() + " stop fail!");
        }
    }

    @Override
    public String getUUID() {
        if (StringUtils.isNotEmpty(uuid)) {
            return uuid;
        }
        if (StringUtils.isNotEmpty(site.getDomain())) {
            return site.getDomain();
        }
        uuid = UUID.randomUUID().toString();
        return uuid;
    }

    @Override
    public Site getSite() {
        return site;
    }

    /**
     * Add urls with information to crawl.<br>
     *
     * @param requests requests
     * @return this
     */
    public Spider addRequest(Request... requests) {
        for (Request request : requests) {
            addRequest(request);
        }
        signalNewUrl();
        return this;
    }

    /**
     * start with more than one threads
     *
     * @param threadNum threadNum
     * @return this
     */
    public Spider thread(int threadNum) {
        checkIfRunning();
        if (threadNum < 1) {
            throw new IllegalArgumentException("threadNum should be more than one!");
        }
        this.threadNum = threadNum;
        return this;
    }

    /**
     * start with more than one threads
     *
     * @param executorService executorService to run the spider
     * @param threadNum       threadNum
     * @return this
     */
    public Spider thread(ExecutorService executorService, int threadNum) {
        checkIfRunning();
        if (threadNum < 1) {
            throw new IllegalArgumentException("threadNum should be more than one!");
        }

        this.threadNum = threadNum;
        this.executorService = executorService;
        return this;
    }

    /**
     * Exit when complete. <br>
     * True: exit when all url of the site is downloaded. <br>
     * False: not exit until call stop() manually.<br>
     *
     * @param exitWhenComplete exitWhenComplete
     * @return this
     */
    public Spider setExitWhenComplete(boolean exitWhenComplete) {
        this.exitWhenComplete = exitWhenComplete;
        return this;
    }

    /**
     * Whether add urls extracted to download.<br>
     * Add urls to download when it is true, and just download seed urls when it is false. <br>
     * DO NOT set it unless you know what it means!
     *
     * @param spawnUrl spawnUrl
     * @return this
     * @since 0.4.0
     */
    public Spider setSpawnUrl(boolean spawnUrl) {
        this.spawnUrl = spawnUrl;
        return this;
    }

    /***
     * Set executor service when spider not running.
     */
    public Spider setExecutorService(ExecutorService executorService) {
        checkIfRunning();
        this.executorService = executorService;
        return this;
    }

    /**
     * Set empty listener.
     *
     * @param spiderListeners spider listener.
     */
    public Spider setSpiderListeners(List<SpiderListener> spiderListeners) {
        this.spiderListeners = spiderListeners;
        return this;
    }

    /**
     * Set wait time when no url is polled.
     *
     * @param emptySleepTime In MILLISECONDS.
     */
    public void setEmptySleepTime(long emptySleepTime) {
        if (emptySleepTime < 1) {
            throw new IllegalArgumentException("emptySleepTime should be more than zero!");
        }
        this.emptySleepTime = emptySleepTime;
    }

    /**
     * Get thread count which is running
     *
     * @return thread count which is running
     * @since 0.4.1
     */
    public int getThreadAlive() {
        return threadPool == null ? 0 : threadPool.getThreadAlive();
    }

    /**
     * Get running status by spider.
     *
     * @return running status
     * @see Status
     * @since 0.4.1
     */
    public Status getStatus() {
        return Status.fromValue(status.get());
    }

    /**
     * Get page count downloaded by spider.
     *
     * @return total downloaded page count
     * @since 0.4.1
     */
    public long getPageCount() {
        return pageCount.get();
    }

    public enum Status {
        Init(0), Running(1), Stopped(2);

        Status(int value) {
            this.value = value;
        }

        @Getter
        private final int value;

        public static Status fromValue(int value) {
            for (Status status : Status.values()) {
                if (status.getValue() == value) {
                    return status;
                }
            }
            //default value
            return Init;
        }
    }
}
