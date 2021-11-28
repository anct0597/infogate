package vn.infogate.ispider.extension.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * @author anct
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonSpiderConfig {

    private int thread;

    // 0: inactive, 1: active
    private int status;

    private DownloaderType downloader = DownloaderType.DEFAULT;

    private JsonPageModel pageModel;

    private String phantomJs = "spider-web/config/crawl.js";

    public enum DownloaderType {
        DEFAULT, PHANTOMJS, SELENIUM
    }
}
