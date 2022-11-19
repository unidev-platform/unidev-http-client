package com.unidev.http;

import com.unidev.httpclient.apache.HTTPClient;
import com.unidev.platform.Randoms;
import com.unidev.platform.Strings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


public class HttpClientTest {

    HTTPClient httpClient;

    @BeforeEach
    void init() {
        httpClient = new HTTPClient(new Randoms(), new Strings());
    }

    @Test
    public void testHttpGet() throws IOException {
        assertNull(httpClient.getLastResponseHeaders());
        String page = httpClient.get("http://google.com");
        assertNotNull(page);
        assertNotNull(httpClient.getLastResponseHeaders());
    }

}
