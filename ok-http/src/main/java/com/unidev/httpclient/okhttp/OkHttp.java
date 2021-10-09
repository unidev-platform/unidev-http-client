package com.unidev.httpclient.okhttp;

import com.unidev.httpclient.SocksSSLSocketFactory;
import com.unidev.httpclient.SocksSocketFactory;
import com.unidev.httpclient.TrustAllX509TrustManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Class to do http requests
 */
public class OkHttp {

    public static OkHttpClient.Builder builder() {
        return new OkHttpClient.Builder();
    }

    /**
     * Configure socks proxy.
     */
    public static OkHttpClient.Builder socksProxy(OkHttpClient.Builder builder, String ip, int port) {
        builder.socketFactory(new SocksSocketFactory(ip, port));
        builder.sslSocketFactory(new SocksSSLSocketFactory(ip, port), TrustAllX509TrustManager.INSTANCE);
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

    public static OkHttpClient.Builder userAgent(OkHttpClient.Builder builder, String userAgent) {
        builder.addNetworkInterceptor(chain -> chain.proceed(
                chain.request()
                        .newBuilder()
                        .header("User-Agent", userAgent)
                        .build()
        ));
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

}
