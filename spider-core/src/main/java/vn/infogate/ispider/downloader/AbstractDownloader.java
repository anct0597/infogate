package vn.infogate.ispider.downloader;


import vn.infogate.ispider.Request;
import vn.infogate.ispider.Site;
import vn.infogate.ispider.selector.Html;

/**
 * Base class of downloader with some common methods.
 *
 * @author code4crafter@gmail.com
 * @since 0.5.0
 */
public abstract class AbstractDownloader implements Downloader {

    /**
     * A simple method to download a url.
     *
     * @param url url
     * @return html
     */
    public Html download(String url) {
        return download(url, null);
    }

    /**
     * A simple method to download a url.
     *
     * @param url     url
     * @param charset charset
     * @return html
     */
    public Html download(String url, String charset) {
        var page = download(new Request(url), Site.me().setCharset(charset).toTask());
        return page.getHtml();
    }

    public abstract void onSuccess(Request request);

    public abstract void onError(Request request);

}
