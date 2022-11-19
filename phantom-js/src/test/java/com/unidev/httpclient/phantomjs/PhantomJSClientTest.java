package com.unidev.httpclient.phantomjs;

import com.unidev.platform.Processes;
import com.unidev.httpclient.apache.PhantomJSClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Phantom JS client test, require phantomjs binary installed
 */
public class PhantomJSClientTest {
    private PhantomJSClient phantomJSClient;

    @BeforeEach
    void init() {
        phantomJSClient = new PhantomJSClient("/bin/phantomjs", new Processes());
    }

    @Test
    public void testHttpGet() throws Exception {
        String content = phantomJSClient.runScript("var page = require('webpage').create();\n" +
                "page.open('http://google.com', function() {\n" +
                "    setTimeout(function() {\n" +
                "        console.log(page.content);\n" +
                "        phantom.exit();\n" +
                "    }, 5000);\n" +
                "});\n");
        System.out.println("Content: " + content);

        assertThat(content).isNotBlank();
    }
}
