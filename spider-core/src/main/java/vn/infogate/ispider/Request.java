package vn.infogate.ispider;

import lombok.Getter;
import vn.infogate.ispider.downloader.Downloader;
import vn.infogate.ispider.model.HttpRequestBody;
import vn.infogate.ispider.scheduler.PriorityScheduler;
import vn.infogate.ispider.utils.Experimental;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Object contains url to crawl.<br>
 * It contains some additional information.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
@Getter
public class Request implements Serializable {

    private static final long serialVersionUID = 2062192774891352043L;
    public static final String CYCLE_TRIED_TIMES = "_cycle_tried_times";

    /**
     * Request url.
     */
    private String url;

    /**
     * Request method.
     */
    private String method;

    /**
     * Request body.
     */
    private HttpRequestBody requestBody;
    /**
     * Charset of site.
     */
    private String charset;

    /**
     * this req use this downloader
     */
    private Downloader downloader;
    /**
     * Store additional information in extras.
     */
    private Map<String, Object> extras;
    /**
     * cookies for current url, if not set use Site's cookies
     */
    private final Map<String, String> cookies = new HashMap<>(5);
    /**
     * Header for site.
     */
    private final Map<String, String> headers = new HashMap<>(3);

    /**
     * Priority of the request.<br>
     * The bigger will be processed earlier. <br>
     *
     * @see PriorityScheduler
     */
    private long priority;

    /**
     * When it is set to TRUE, the downloader will not try to parse response body to text.
     */
    private boolean binaryContent;

    public Request() {
    }

    public Request(String url) {
        this.url = url;
    }

    /**
     * Set the priority of request for sorting.<br>
     * Need a scheduler supporting priority.<br>
     *
     * @param priority priority
     * @return this
     * @see PriorityScheduler
     */
    @Experimental
    public Request setPriority(long priority) {
        this.priority = priority;
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T getExtra(String key) {
        return extras == null ? null : (T) extras.get(key);
    }

    public <T> Request putExtra(String key, T value) {
        if (extras == null) extras = new HashMap<>(2);
        extras.put(key, value);
        return this;
    }

    public Request setExtras(Map<String, Object> extras) {
        this.extras = extras;
        return this;
    }

    public Request setUrl(String url) {
        this.url = url;
        return this;
    }

    public Request setMethod(String method) {
        this.method = method;
        return this;
    }

    public Request addCookie(String name, String value) {
        cookies.put(name, value);
        return this;
    }

    public Request addHeader(String name, String value) {
        headers.put(name, value);
        return this;
    }

    public Request setBinaryContent(boolean binaryContent) {
        this.binaryContent = binaryContent;
        return this;
    }

    public Request setCharset(String charset) {
        this.charset = charset;
        return this;
    }

    public Request setDownloader(Downloader downloader) {
        this.downloader = downloader;
        return this;
    }

    public Request setRequestBody(HttpRequestBody requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (method != null ? method.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return Objects.equals(url, request.url) && Objects.equals(method, request.method);
    }

    @Override
    public String toString() {
        return "Request{" +
                "url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", extras=" + extras +
                ", priority=" + priority +
                ", headers=" + headers +
                ", cookies=" + cookies +
                '}';
    }

}
