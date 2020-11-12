package org.lamisplus.modules.base.service;

import com.foreach.across.core.annotations.Exposed;
import com.princexml.PrinceControl;
import com.sun.jna.Library;
import com.sun.jna.Native;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.lamisplus.modules.base.config.ApplicationProperties;
import org.lamisplus.modules.base.module.ModuleUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Service
@Exposed
@RequiredArgsConstructor
@Slf4j
public class PrinceXMLService {
    private static PrinceControl CONTROL;
    private final ApplicationProperties applicationProperties;


    public boolean convert(byte[] xmlInput, OutputStream pdfOutput) throws IOException {
        return CONTROL.convert(xmlInput, pdfOutput);
    }

    public boolean convert(InputStream xmlInput, OutputStream pdfOutput) throws IOException {
        return CONTROL.convert(xmlInput, pdfOutput);
    }

    @SneakyThrows
    @PreDestroy
    public void stop() {
        CONTROL.stop();
    }

    @SneakyThrows
    @PostConstruct
    public void init() {
        try {
            FileUtils.cleanDirectory(new File(applicationProperties.getTempDir() + "/princexml"));
        } catch (Exception ignored) {
        }

        String os = SystemUtils.IS_OS_LINUX ? "linux64" : "win32";
        FileUtils.forceMkdirParent(new File(applicationProperties.getTempDir() + "/princexml"));
        ModuleUtils.extract("/princexml/" + os, applicationProperties.getTempDir() + "/princexml");
        File file = new File(applicationProperties.getTempDir() + "/princexml/bin/prince");
        new Thread(() -> {
            if (SystemUtils.IS_OS_LINUX) {
                CLibrary libc = (CLibrary) Native.loadLibrary("c", CLibrary.class);
                libc.chmod(file.getAbsolutePath(), 493);
            }
            try {
                Thread.sleep(15 * 1000);
            } catch (Exception ignored) {
            }
            CONTROL = new PrinceControl(file.getAbsolutePath());
            try {
                CONTROL.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    interface CLibrary extends Library {
        int chmod(String path, int mode);
    }
}
