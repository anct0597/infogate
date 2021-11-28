package vn.infogate.ispider.core.downloader;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Using with html unit driver.
 */
@Slf4j
public class WebDriverPool {

    private final static int DEFAULT_CAPACITY = 5;
    private final static int STAT_RUNNING = 1;
    private final static int STAT_CLOSED = 2;
    private final AtomicInteger stats = new AtomicInteger(STAT_RUNNING);
    private final int capacity;

    /**
     * store webDrivers created
     */
    private final List<WebDriver> webDriverList = Collections.synchronizedList(new ArrayList<>());

    /**
     * store webDrivers available
     */
    private final BlockingDeque<WebDriver> innerQueue = new LinkedBlockingDeque<>();

    public WebDriverPool(int capacity) {
        this.capacity = capacity;
    }

    public WebDriverPool() {
        this(DEFAULT_CAPACITY);
    }

    public WebDriver get() throws InterruptedException {
        checkRunning();
        WebDriver poll = innerQueue.poll();
        if (poll != null) return poll;

        if (webDriverList.size() < capacity) {
            synchronized (webDriverList) {
                if (webDriverList.size() < capacity) {
                    // add new WebDriver instances into pool
                    var driver = new HtmlUnitDriver();
                    driver.setJavascriptEnabled(false);
                    driver.setDownloadImages(true);
                    innerQueue.add(driver);
                    webDriverList.add(driver);
                }
            }

        }
        return innerQueue.take();
    }

    public void returnToPool(WebDriver webDriver) {
        checkRunning();
        innerQueue.add(webDriver);
    }

    protected void checkRunning() {
        if (!stats.compareAndSet(STAT_RUNNING, STAT_RUNNING)) {
            throw new IllegalStateException("Already closed!");
        }
    }

    public void closeAll() {
        checkRunning();
        for (WebDriver webDriver : webDriverList) {
            log.info("Quit webDriver" + webDriver);
            webDriver.quit();
        }
    }

}
