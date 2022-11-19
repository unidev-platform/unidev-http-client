package com.unidev.httpclient.apache;

import com.unidev.httpclient.socks.PlainSocksSocketFactory;
import com.unidev.httpclient.socks.SSLSocksSocketFactory;
import com.unidev.platform.Randoms;
import com.unidev.platform.Strings;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Basic HTTP client, used to get stuff from web pages
 */
public class HTTPClient {

    protected Randoms randoms;

    protected Strings strings;

    protected PoolingHttpClientConnectionManager connectionManager = null;
    protected HttpClient httpClient;

    protected int connectionTimeout = 10000;
    protected HttpResponse rsp;
    protected Header[] lastResponseHeaders;
    protected HttpContext context = new BasicHttpContext();
    protected String currentUrl = "";
    protected BasicCookieStore basicCookieStore = new BasicCookieStore();

    public HTTPClient(Randoms randoms, Strings strings) {
        this.randoms = randoms;
        this.strings = strings;
        init(HTTPClientUtils.USER_AGENTS);
    }

    public HTTPClient(Randoms randoms, Strings strings, String userAgent) {
        this.randoms = randoms;
        this.strings = strings;
        init(userAgent);
    }

    /**
     * Re-use existing http client builder.
     */
    public void init(HttpClientBuilder httpClientBuilder) {
        if (connectionManager != null) {
            httpClientBuilder.setConnectionManager(connectionManager);
        }
        httpClientBuilder.setDefaultCookieStore(basicCookieStore);
        httpClientBuilder.setDefaultRequestConfig(buildRequestConfig().build());
        httpClient = httpClientBuilder.build();
    }

    /**
     * Create non-proxy based http client with specified user agent(optional)
     *
     * @param userAgent optional user agent name
     */
    public void init(String userAgent) {
        HttpClientBuilder builder = HttpClients.custom();
        builder.setUserAgent(userAgent);
        init(builder);
    }

    public void init(String socksIP, int socksPort, String userAgent) {
        createProxyConnectionManager(socksIP, socksPort);
        init(userAgent);
    }

    public void init(String socksIP, int socksPort, List<String> userAgents) {
        createProxyConnectionManager(socksIP, socksPort);
        init(userAgents);
    }

    public void init(String socksIP, int socksPort) {
        createProxyConnectionManager(socksIP, socksPort);
        init(Strings.EMPTY);
    }


    /**
     * Build request configuration
     */
    protected RequestConfig.Builder buildRequestConfig() {
        return RequestConfig.custom().setCircularRedirectsAllowed(true)
                .setConnectTimeout(connectionTimeout)
                .setConnectionRequestTimeout(connectionTimeout)
                .setSocketTimeout(connectionTimeout);
    }

    /**
     * Create default HTTPClient with random user agent and no proxy
     */
    public void init(List<String> userAgents) {
        if (!CollectionUtils.isEmpty(userAgents)) {
            init(randoms.randomValue(userAgents));
        } else {
            init("");
        }
    }

    public StatusLine head(String url, Header... headers) throws IOException {
        HttpHead httpHead = new HttpHead(url);
        copyHeaders(httpHead, headers);
        rsp = httpClient.execute(httpHead, context);

        return rsp.getStatusLine();
    }

    public String get(String url, Header... headers) throws IOException {
        HttpGet httpget = new HttpGet(url);
        copyHeaders(httpget, headers);

        rsp = httpClient.execute(httpget, context);

        HttpUriRequest currentReq = (HttpUriRequest) context.getAttribute(
                ExecutionContext.HTTP_REQUEST);
        HttpHost currentHost = (HttpHost) context.getAttribute(
                ExecutionContext.HTTP_TARGET_HOST);
        currentUrl = (currentReq.getURI().isAbsolute()) ? currentReq.getURI().toString()
                : (currentHost.toURI() + currentReq.getURI());

        HttpEntity entity = rsp.getEntity();
        lastResponseHeaders = rsp.getAllHeaders();

        if (connectionManager != null) {
            connectionManager.closeExpiredConnections();
            connectionManager.closeIdleConnections(1, TimeUnit.SECONDS);
        }
        return (EntityUtils.toString(entity));
    }

    public HttpEntity getAsEntity(String url, Header... headers)
            throws ClientProtocolException, IOException {
        HttpGet httpget = new HttpGet(url);
        copyHeaders(httpget, headers);
        rsp = httpClient.execute(httpget, context);

        HttpEntity entity = rsp.getEntity();

        lastResponseHeaders = rsp.getAllHeaders();

        if (connectionManager != null) {
            connectionManager.closeExpiredConnections();
            connectionManager.closeIdleConnections(1, TimeUnit.SECONDS);
        }
        return entity;
    }

    public byte[] getAsStream(String url, Header... headers) throws IOException {
        HttpGet httpget = new HttpGet(url);
        copyHeaders(httpget, headers);
        rsp = httpClient.execute(httpget, context);

        HttpEntity entity = rsp.getEntity();

        if (connectionManager != null) {
            connectionManager.closeExpiredConnections();
            connectionManager.closeIdleConnections(1, TimeUnit.SECONDS);
        }

        return EntityUtils.toByteArray(entity);
    }

    public String post(String url, HttpEntity data, Header... headers) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        copyHeaders(httpPost, headers);

        httpPost.setEntity(data);

        rsp = httpClient.execute(httpPost, context);

        HttpUriRequest currentReq = (HttpUriRequest) context.getAttribute(
                ExecutionContext.HTTP_REQUEST);
        HttpHost currentHost = (HttpHost) context.getAttribute(
                ExecutionContext.HTTP_TARGET_HOST);
        currentUrl = (currentReq.getURI().isAbsolute()) ? currentReq.getURI().toString()
                : (currentHost.toURI() + currentReq.getURI());

        HttpEntity entity = rsp.getEntity();

        lastResponseHeaders = rsp.getAllHeaders();
        if (connectionManager != null) {
            connectionManager.closeExpiredConnections();
            connectionManager.closeIdleConnections(1, TimeUnit.SECONDS);
        }

        return (EntityUtils.toString(entity));
    }

    public HttpEntity postAsEntity(String url, Header... headers)
            throws ClientProtocolException, IOException {
        HttpPost httpPost = new HttpPost(url);
        copyHeaders(httpPost, headers);

        rsp = httpClient.execute(httpPost, context);

        HttpEntity entity = rsp.getEntity();

        lastResponseHeaders = rsp.getAllHeaders();

        if (connectionManager != null) {
            connectionManager.closeExpiredConnections();
            connectionManager.closeIdleConnections(1, TimeUnit.SECONDS);
        }
        return entity;
    }

    public HttpEntity delete(String url, Header... headers) throws IOException {
        HttpDelete httpDelete = new HttpDelete(url);
        copyHeaders(httpDelete, headers);

        rsp = httpClient.execute(httpDelete, context);
        return rsp.getEntity();
    }

    public HttpEntity put(String url, HttpEntity data, Header... headers) throws IOException {
        HttpPut put = new HttpPut(url);
        copyHeaders(put, headers);

        put.setEntity(data);

        rsp = httpClient.execute(put, context);
        return rsp.getEntity();
    }

    public HttpEntity patch(String url, HttpEntity data, Header... headers) throws IOException {
        HttpPatch put = new HttpPatch(url);
        copyHeaders(put, headers);

        put.setEntity(data);

        rsp = httpClient.execute(put, context);
        return rsp.getEntity();
    }

    protected HTTPClient copyHeaders(AbstractHttpMessage request, Header... headers) {
        for (Header h : headers) {
            request.addHeader(h);
        }
        return this;
    }

    public String getHeaderValue(String header) {
        String v = null;
        for (Header h : lastResponseHeaders) {
            if (h.getName().equalsIgnoreCase(header)) {
                v = h.getValue();
                break;
            }
        }
        return v;
    }

    public String getLastResponseHeadersAsString() {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < lastResponseHeaders.length; i++) {
            result.append(lastResponseHeaders[i] + "\n");
        }

        return result.toString();
    }

    public String getCookiesStoreAsString() {
        StringBuilder stringBuilder = new StringBuilder();
        List<Cookie> cookies = basicCookieStore.getCookies();
        for (Cookie cookie : cookies) {
            stringBuilder.append(cookie.toString() + "\n");
        }
        return stringBuilder.toString();
    }

    public Cookie getCookie(String name) {
        List<Cookie> cookies = basicCookieStore.getCookies();
        for (Cookie cookie : cookies) {
            if (name.equalsIgnoreCase(cookie.getName())) {
                return cookie;
            }
        }
        return null;
    }

    private void createProxyConnectionManager(String socksIP, int socksPort) {
        connectionManager = new PoolingHttpClientConnectionManager(
                RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("http", new PlainSocksSocketFactory(socksIP, socksPort))
                        .register("https", new SSLSocksSocketFactory(socksIP, socksPort))
                        .build()
        );
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public HttpResponse getHTTPResponse() {
        return rsp;
    }

    public String getCurrentUrl() {
        return currentUrl;
    }

    public void close() {
        httpClient.getConnectionManager().shutdown();
    }

    public Header[] getLastResponseHeaders() {
        return lastResponseHeaders;
    }

    public StatusLine getStatusLine() {
        return rsp.getStatusLine();
    }

    public int getStatusCode() {
        return getStatusLine().getStatusCode();
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public BasicCookieStore getCookieStore() {
        return basicCookieStore;
    }

    public void setCookieStore(BasicCookieStore basicCookieStore) {
        this.basicCookieStore = basicCookieStore;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public HttpResponse getRsp() {
        return rsp;
    }

    public void setRsp(HttpResponse rsp) {
        this.rsp = rsp;
    }

    public void setLastResponseHeaders(Header[] lastResponseHeaders) {
        this.lastResponseHeaders = lastResponseHeaders;
    }

    public HttpContext getContext() {
        return context;
    }

    public void setContext(HttpContext context) {
        this.context = context;
    }

    public void setCurrentUrl(String currentUrl) {
        this.currentUrl = currentUrl;
    }

    public BasicCookieStore getBasicCookieStore() {
        return basicCookieStore;
    }

    public void setBasicCookieStore(BasicCookieStore basicCookieStore) {
        this.basicCookieStore = basicCookieStore;
    }

    public PoolingHttpClientConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public void setConnectionManager(
            PoolingHttpClientConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }
}
