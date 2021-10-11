package com.unidev.httpclient.okhttp;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OkHttpTest {

    @Test
    void testHttpQuery() {
        String page = new OkHttp().get("http://google.com");
        System.out.println(page);
        assertNotNull(page);
    }

    @Test
    void testProxyQuery() {
        String page = new OkHttp().get("http://canyouseeme.org/");
        String ip = StringUtils.substringBetween(page, "id=\"ip\" type=\"text\" value=\"", "\"");
        System.out.println(ip);
        assertNotNull(ip);

        OkHttpClient.Builder builder = OkHttp.builder();
        String proxyPage = new OkHttp(OkHttp.socksProxy(builder, "10.10.10.81", 9001)).get("http://canyouseeme.org/");
        String proxyIp = StringUtils.substringBetween(proxyPage, "id=\"ip\" type=\"text\" value=\"", "\"");
        System.out.println(proxyIp);

        assertNotNull(proxyIp);
        assertNotEquals(proxyIp, ip);
    }

    @Test
    void postRequest() {
        RequestBody formBody = new FormBody.Builder()
                .add("key", "value")
                .build();
        String output = new OkHttp().post("http://canyouseeme.org/", formBody);
        System.out.println(output);
    }

}
