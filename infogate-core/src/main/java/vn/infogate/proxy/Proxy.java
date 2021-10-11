package vn.infogate.proxy;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Getter
@Setter
public class Proxy {

    private String scheme;
    private final String host;
    private final int port;
    private String username;
    private String password;

    public static Proxy create(URI uri) {
        Proxy proxy = new Proxy(uri.getHost(), uri.getPort(), uri.getScheme());
        String userInfo = uri.getUserInfo();
        if (userInfo != null) {
            String[] up = userInfo.split(":");
            if (up.length == 1) {
                proxy.username = up[0].isEmpty() ? null : up[0];
            } else {
                proxy.username = up[0].isEmpty() ? null : up[0];
                proxy.password = up[1].isEmpty() ? null : up[1];
            }
        }
        return proxy;
    }

    public Proxy(String host, int port) {
        this(host, port, null);
    }

    public Proxy(String host, int port, String scheme) {
        this.host = host;
        this.port = port;
        this.scheme = scheme;
    }

    public Proxy(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public URI toURI() {
        final StringBuilder userInfoBuffer = new StringBuilder();
        if (username != null) {
            userInfoBuffer.append(urlEncode(username));
        }
        if (password != null) {
            userInfoBuffer.append(":").append(urlEncode(password));
        }
        final String userInfo = StringUtils.defaultIfEmpty(userInfoBuffer.toString(), null);
        URI uri;
        try {
            uri = new URI(scheme, userInfo, host, port, null, null, null);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        return uri;
    }

    private String urlEncode(String s) {
        String enc = StandardCharsets.UTF_8.name();
        try {
            return URLEncoder.encode(s, enc);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Proxy proxy = (Proxy) o;

        return Objects.equals(this.host, proxy.host)
                && Objects.equals(this.port, proxy.port)
                && Objects.equals(this.username, proxy.username)
                && Objects.equals(this.password, proxy.password);
    }

    @Override
    public int hashCode() {
        int result = host != null ? host.hashCode() : 0;
        result = 31 * result + port;
        result = 31 * result + (scheme != null ? scheme.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return this.toURI().toString();
    }

}
