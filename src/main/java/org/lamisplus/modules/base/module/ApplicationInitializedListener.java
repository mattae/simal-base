package org.lamisplus.modules.base.module;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;
import org.lamisplus.modules.base.domain.entities.Module;
import org.lamisplus.modules.base.domain.repositories.ModuleRepository;
import org.lamisplus.modules.base.util.CyclicDependencyException;
import org.lamisplus.modules.base.util.ModuleDependencyResolver;
import org.lamisplus.modules.base.util.UnsatisfiedDependencyException;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApplicationInitializedListener {
    private final ModuleRepository moduleRepository;
    private final ModuleService moduleService;
    private final SimpMessageSendingOperations messagingTemplate;

    @EventListener
    @Async
    public void onApplicationEvent(ServletWebServerInitializedEvent event) {
        LOG.info("Scanning for active modules...");

        List<Module> modules = moduleRepository.findByActiveIsTrueOrderByPriority();
        List<Module> resolved = new ArrayList<>();
        List<Module> unresolved = new ArrayList<>();
        List<Module> started = new ArrayList<>();

        modules.forEach(module -> {
            try {
                ModuleDependencyResolver.resolveDependencies(module, resolved, unresolved);
            } catch (CyclicDependencyException | UnsatisfiedDependencyException e) {
                LOG.error(e.getMessage());
            }
        });
        if (!resolved.isEmpty()) {
            LOG.debug("Starting up active modules ...");
        }

        resolved.forEach(module -> {
            if (!started.contains(module)) {
                started.add(module);
                try {
                    boolean install = false;
                    if (module.getInstallOnBoot() != null && module.getInstallOnBoot()) {
                        install = true;
                    }
                    moduleService.installModule(module, install, true);
                } catch (Exception ignored) {
                }
            }
        });
        int port = event.getApplicationContext().getWebServer().getPort();

        String url = "http://localhost:" + port;
        new BareBonesBrowserLaunch().openURL(url);
        messagingTemplate.convertAndSend("/topic/modules-changed", "Application started");
    }

    static class BareBonesBrowserLaunch {

        final String[] browsers = {"x-www-browser", "google-chrome",
                "firefox", "opera", "epiphany", "konqueror", "conkeror", "midori",
                "kazehakase", "mozilla"};

        // Open the specified web page in the user's default browser
        public void openURL(String url) {
            try {  //attempt to use Desktop library from JDK 1.6+
                Class<?> d = Class.forName("java.awt.Desktop");
                d.getDeclaredMethod("browse",
                        new Class<?>[]{java.net.URI.class}).invoke(
                        d.getDeclaredMethod("getDesktop").invoke(null), URI.create(url));
            } catch (Exception ignore) {  //library not available or failed
                try {
                    if (SystemUtils.IS_OS_MAC) {
                        Class.forName("com.apple.eio.FileManager").getDeclaredMethod(
                                "openURL", new Class<?>[]{String.class}).invoke(null, url);
                    } else if (SystemUtils.IS_OS_WINDOWS)
                        Runtime.getRuntime().exec(
                                "rundll32 url.dll,FileProtocolHandler " + url);
                    else { //assume Unix or Linux
                        String browser = null;
                        for (String b : browsers) {
                            if (browser == null && Runtime.getRuntime().exec(new String[]
                                    {"which", b}).getInputStream().read() != -1) {
                                Runtime.getRuntime().exec(new String[]{browser = b, url});
                            }
                        }
                        if (browser == null) {
                            throw new Exception(Arrays.toString(browsers));
                        }
                    }
                } catch (Exception ex) {
                    LOG.error("Could not open browser: {}", ex.getMessage());
                }
            }
        }
    }
}
