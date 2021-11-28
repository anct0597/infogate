package vn.infogate.ispider.core.downloader;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import vn.infogate.ispider.core.Page;
import vn.infogate.ispider.core.Request;
import vn.infogate.ispider.core.Site;
import vn.infogate.ispider.core.Task;
import vn.infogate.ispider.core.selector.Html;
import vn.infogate.ispider.core.selector.PlainText;

import java.io.Closeable;
import java.io.IOException;

@Slf4j
@NoArgsConstructor
public class HtmlUnitSeleniumDownloader implements Downloader, Closeable {

    private volatile WebDriverPool webDriverPool;

    private int sleepTime = 0;
    private int poolSize = 1;

    public HtmlUnitSeleniumDownloader(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    public HtmlUnitSeleniumDownloader(int sleepTime, int poolSize) {
        this.sleepTime = sleepTime;
        this.poolSize = poolSize;
    }

    @Override
    public Page download(Request request, Task task) {
        checkWebDriverPool();
        WebDriver webDriver;
        try {
            webDriver = webDriverPool.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("interrupted", e);
            return null;
        }
        log.info("downloading page " + request.getUrl());
        webDriver.get(request.getUrl());
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("interrupted", e);
        }
        WebDriver.Options manage = webDriver.manage();
        Site site = task.getSite();
        if (site.getCookies() != null) {
            for (var cookieEntry : site.getCookies().entrySet()) {
                var cookie = new Cookie(cookieEntry.getKey(), cookieEntry.getValue());
                manage.addCookie(cookie);
            }
        }

        /*
         * TODO You can add mouse event or other processes
         *
         * @author: bob.li.0718@gmail.com
         */

        WebElement webElement = webDriver.findElement(By.xpath("/html"));
        String content = webElement.getAttribute("outerHTML");
        Page page = new Page();
        page.setRawText(content);
        page.setHtml(new Html(content, request.getUrl()));
        page.setUrl(new PlainText(request.getUrl()));
        page.setRequest(request);
        webDriverPool.returnToPool(webDriver);
        return page;
    }

    private void checkWebDriverPool() {
        if (webDriverPool == null) {
            synchronized (this) {
                webDriverPool = new WebDriverPool(poolSize);
            }
        }
    }

    @Override
    public void setThread(int thread) {
        this.poolSize = thread;
    }

    @Override
    public void close() throws IOException {
        webDriverPool.closeAll();
    }
}
