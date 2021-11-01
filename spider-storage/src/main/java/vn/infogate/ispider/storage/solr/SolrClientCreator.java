package vn.infogate.ispider.storage.solr;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HeaderElement;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HttpContext;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import vn.infogate.ispider.storage.solr.exception.InitSolrClientException;

import java.net.URL;

@Slf4j
public final class SolrClientCreator {

    private SolrClientCreator() {
    }

    public static HttpSolrClient create(String remote, String username, String password, int timeout) {
        try {
            var clientBuilder = HttpClientBuilder.create();
            clientBuilder.setMaxConnPerRoute(128);
            clientBuilder.setMaxConnTotal(32);
            var requestBuilder = RequestConfig.custom();
            requestBuilder = requestBuilder.setConnectTimeout(timeout);
            requestBuilder = requestBuilder.setConnectionRequestTimeout(timeout);
            requestBuilder = requestBuilder.setConnectionRequestTimeout(timeout);
            clientBuilder.setDefaultRequestConfig(requestBuilder.build());
            clientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(10, true));
            var url = new URL(remote);
            var credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(new AuthScope(url.getHost(), url.getPort()), new UsernamePasswordCredentials(username, password));
            clientBuilder.setDefaultRequestConfig(requestBuilder.build())
                    .setDefaultCredentialsProvider(credentialsProvider)
                    .addInterceptorFirst(new PreemptiveAuthInterceptor());

            var httpClient = HttpClients.custom()
                    .setKeepAliveStrategy(getConnectionKeepAliveStrategy())
                    .setDefaultConnectionConfig(getConnectionConfig())
                    .build();
            var solrBuilder = new HttpSolrClient.Builder(remote);
            solrBuilder.withHttpClient(httpClient);
            solrBuilder.allowCompression(true);
            var client = solrBuilder.build();
            log.info(client.getBaseURL() + ": Create Successful! ");
            return client;
        } catch (Exception e) {
            throw new InitSolrClientException(e.getMessage(), e);
        }
    }

    private static ConnectionKeepAliveStrategy getConnectionKeepAliveStrategy() {
        return (response, context) -> {
            var it = new BasicHeaderElementIterator(response.headerIterator("Keep-Alive"));
            String param;
            String value;
            do {
                if (!it.hasNext()) {
                    HttpHost target = (HttpHost) context.getAttribute("http.target_host");
                    return target.getHostName().equalsIgnoreCase("localhost") ? 5000L : 30000L;
                }
                HeaderElement he = it.nextElement();
                param = he.getName();
                value = he.getValue();
            } while (value == null || !param.equalsIgnoreCase("timeout"));

            return Long.parseLong(value) * 1000L;
        };
    }

    private static ConnectionConfig getConnectionConfig() {
        return ConnectionConfig.custom().setBufferSize(4128).build();
    }

    private static class PreemptiveAuthInterceptor implements HttpRequestInterceptor {
        PreemptiveAuthInterceptor() {
        }

        public void process(HttpRequest request, HttpContext context) {
            var authState = (AuthState) context.getAttribute("http.auth.target-scope");
            if (authState.getAuthScheme() == null) {
                var credentialsProvider = (CredentialsProvider) context.getAttribute("http.auth.credentials-provider");
                var targetHost = (HttpHost) context.getAttribute("http.target_host");
                var authScope = new AuthScope(targetHost.getHostName(), targetHost.getPort());
                authState.update(new BasicScheme(), credentialsProvider.getCredentials(authScope));
            }
        }
    }
}