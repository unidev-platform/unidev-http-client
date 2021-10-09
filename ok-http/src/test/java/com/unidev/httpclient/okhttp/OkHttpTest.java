package com.unidev.httpclient.okhttp;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

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

        String proxyPage = new OkHttp(OkHttp.socksProxy(OkHttp.builder(), "10.10.10.81", 9001)).get("http://canyouseeme.org/");
        String proxyIp = StringUtils.substringBetween(page, "id=\"ip\" type=\"text\" value=\"", "\"");
        System.out.println(proxyIp);
    }

}
