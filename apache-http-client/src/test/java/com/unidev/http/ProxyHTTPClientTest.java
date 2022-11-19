package com.unidev.http;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.unidev.platform.Randoms;
import com.unidev.platform.Strings;
import com.unidev.httpclient.apache.HTTPClient;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class ProxyHTTPClientTest {

    HTTPClient httpClient;

    @BeforeEach
    void init() {
        httpClient = new HTTPClient(new Randoms(), new Strings());
    }

    @Test
    public void testHttp() throws IOException {
        String page = httpClient.get("http://canyouseeme.org/");
        String ip = Strings.substringBetween(page, "id=\"ip\" type=\"text\" value=\"", "\"");
        assertThat(ip).isNotBlank();

        httpClient.init("10.10.10.81", 9001);
        page = httpClient.get("http://canyouseeme.org/");
        String proxyIP = Strings.substringBetween(page, "id=\"ip\" type=\"text\" value=\"", "\"");
        assertThat(proxyIP).isNotBlank();
        assertThat(proxyIP).isNotEqualToIgnoringCase(ip);

    }

    @Test
    public void testHttps() throws IOException {
        String page = httpClient.get("https://www.ip-secrets.com");
        String ip = Strings.substringBetween(page, "Your current IP-Adress: <font color=\"#FFFF33\">", "&nbsp;");
        assertThat(ip).isNotBlank();

        httpClient.init("10.10.10.81", 9001);
        page = httpClient.get("https://www.ip-secrets.com");
        String proxyIP = Strings.substringBetween(page, "Your current IP-Adress: <font color=\"#FFFF33\">", "&nbsp;");
        assertThat(proxyIP).isNotBlank();
        assertThat(proxyIP).isNotEqualToIgnoringCase(ip);

    }

}
