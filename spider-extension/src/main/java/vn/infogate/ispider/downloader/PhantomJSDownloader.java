package vn.infogate.ispider.downloader;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import vn.infogate.ispider.Page;
import vn.infogate.ispider.Request;
import vn.infogate.ispider.Task;
import vn.infogate.ispider.selector.PlainText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


@Slf4j
@Getter
@Setter
public class PhantomJSDownloader extends AbstractDownloader {

    private int retryNum;
    private final String crawlJsPath;
    private static String phantomJsCommand = "spider-web/phantomjs/bin/phantomjs.exe"; // default

    public PhantomJSDownloader() {
        this.crawlJsPath = "spider-web/config/crawl.js";// default
    }

    public PhantomJSDownloader(String crawlJsPath) {
        this.crawlJsPath = crawlJsPath;
    }

    @Override
    public Page download(Request request, Task task) {
        if (log.isInfoEnabled()) {
            log.info("downloading page: " + request.getUrl());
        }
        String content = getPage(request);
        if (content.contains("HTTP request failed")) {
            for (int i = 1; i <= retryNum; i++) {
                content = getPage(request);
                if (!content.contains("HTTP request failed")) {
                    break;
                }
            }
            if (content.contains("HTTP request failed")) {
                //when failed
                Page page = new Page();
                page.setRequest(request);
                return page;
            }
        }

        Page page = new Page();
        page.setRawText(content);
        page.setUrl(new PlainText(request.getUrl()));
        page.setRequest(request);
        page.setStatusCode(200);
        return page;
    }

    @Override
    public void setThread(int threadNum) {
        // Just implement
    }

    protected String getPage(Request request) {
        try {
            String url = request.getUrl();
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(phantomJsCommand + " " + crawlJsPath + " " + url);
            InputStream is = process.getInputStream();
            var br = new BufferedReader(new InputStreamReader(is));
            var strBuilder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                strBuilder.append(line).append("\n");
            }
            return strBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onSuccess(Request request) {

    }

    @Override
    public void onError(Request request) {

    }
}
