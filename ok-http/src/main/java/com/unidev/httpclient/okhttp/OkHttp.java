package com.unidev.httpclient.okhttp;

import okhttp3.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

/**
 * Class to do http requests
 */
public class OkHttp {

    public static OkHttpClient.Builder builder() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        return builder;
    }

    /**
     * Configure socks proxy.
     */
    public static OkHttpClient.Builder socksProxy(OkHttpClient.Builder builder, String ip, int port) {
        builder.proxy(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(ip, port)));
        return builder;
    }

    /**
     * Set request timeout in MS
     */
    public static OkHttpClient.Builder timeout(OkHttpClient.Builder builder, int timeout) {
        builder.callTimeout(timeout, TimeUnit.MILLISECONDS);
        builder.connectTimeout(timeout, TimeUnit.MILLISECONDS);
        builder.readTimeout(timeout, TimeUnit.MILLISECONDS);
        builder.writeTimeout(timeout, TimeUnit.MILLISECONDS);
        return builder;
    }

    /**
     * Configure User Agent
     */
    public static OkHttpClient.Builder userAgent(OkHttpClient.Builder builder, String userAgent) {
        builder.addNetworkInterceptor(chain -> chain.proceed(
                chain.request()
                        .newBuilder()
                        .header("User-Agent", userAgent)
                        .build()
        ));
        return builder;
    }

    /**
     * Set default connection pool
     */
    public static OkHttpClient.Builder connectionPool(OkHttpClient.Builder builder, int connections, int keepAliveSeconds) {
        builder.connectionPool(new ConnectionPool(connections, keepAliveSeconds, TimeUnit.SECONDS));
        return builder;
    }

    public static OkHttpClient.Builder connectionPool(OkHttpClient.Builder builder, ConnectionPool connectionPool) {
        builder.connectionPool(connectionPool);
        return builder;
    }

    public static OkHttpClient httpClient(OkHttpClient.Builder builder) {
        return new OkHttpClient(builder);
    }

    private final OkHttpClient client;

    public OkHttp(OkHttpClient client) {
        this.client = client;
    }

    public OkHttp(OkHttpClient.Builder builder) {
        this.client = new OkHttpClient(builder);
    }

    public OkHttp() {
        this.client = new OkHttpClient();
    }

    public OkHttpClient getClient() {
        return this.client;
    }

    /**
     * Simplified get request.
     */
    public String get(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Simplified post request.
     */
    public String post(String url, RequestBody body) {
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
