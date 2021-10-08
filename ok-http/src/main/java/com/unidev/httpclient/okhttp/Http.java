package com.unidev.httpclient.okhttp;

import okhttp3.OkHttpClient;

/**
 * Class to do http requests
 */
public class Http {

    public static OkHttpClient.Builder addUserAgent(OkHttpClient.Builder builder, String userAgent) {

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
