package com.unidev.httpclient.okhttp;

import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

/**
 * Class to do http requests
 */
public class Http {

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

    private final OkHttpClient okHttpClient;

    public Http(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

}
