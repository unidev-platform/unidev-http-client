/*
  Copyright (c) 2018 Denis O <denis.o@linux.com>

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package com.unidev.http;

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
