package com.unidev.httpclient.apache;

import com.unidev.platform.Processes;
import com.unidev.platform.model.ProcessToRun;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.Charset;

/**
 * PhantomJS based client
 */
@NoArgsConstructor
@AllArgsConstructor
public class PhantomJSClient {

    @Getter
    @Setter
    private String phantomBinary = "phantomjs";

    @Getter
    @Setter
    private Processes processes;

    public String runScript(String script) throws Exception {
        File scriptFile = null;
        try {
            scriptFile = File.createTempFile("phantomjs", ".js");
            scriptFile.deleteOnExit();
            FileUtils.write(scriptFile, script, Charset.defaultCharset());
            ProcessToRun processToRun = ProcessToRun.builder().command(phantomBinary + " " + scriptFile.getAbsolutePath()).build();
            return processes.runProcess(processToRun);
        } finally {
            FileUtils.deleteQuietly(scriptFile);
        }
    }
}
