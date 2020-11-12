package org.lamisplus.modules.base.web.rest;

import io.github.jhipster.web.util.ResponseUtil;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.base.domain.entities.Menu;
import org.lamisplus.modules.base.domain.entities.Module;
import org.lamisplus.modules.base.domain.entities.WebModule;
import org.lamisplus.modules.base.domain.enumeration.MenuLevel;
import org.lamisplus.modules.base.domain.enumeration.MenuType;
import org.lamisplus.modules.base.domain.repositories.MenuRepository;
import org.lamisplus.modules.base.domain.repositories.ModuleRepository;
import org.lamisplus.modules.base.domain.repositories.WebModuleRepository;
import org.lamisplus.modules.base.module.ModuleResponse;
import org.lamisplus.modules.base.module.ModuleService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class ModuleResource {
    private final WebModuleRepository webModuleRepository;
    private final MenuRepository menuRepository;
    private final ModuleRepository moduleRepository;
    private final ModuleService moduleService;
    //private final RestartEndpoint restartEndpoint;

    @GetMapping("/modules/{id:\\d+}/web-modules")
    @Timed
    //@PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public List<WebModule> getModulesByType(@PathVariable("id") Long id) {
        LOG.debug("Getting web modules for module: {}", id);

        Module module = moduleRepository.findById(id).orElse(null);
        if (module != null) {
            return webModuleRepository.findByModule(module);
        }
        return new ArrayList<>();
    }

    /*@GetMapping("/modules/restart")
    public Object restart() {
        return restartEndpoint.restart();
    }*/

    @GetMapping("/modules/{id:\\d+}")
    @Timed
    public ResponseEntity<Module> getModule(@PathVariable("id") Long id) {
        LOG.debug("Getting module: {}", id);

        Optional<Module> module = moduleRepository.findById(id);
        module = module.map(m -> {
            m.setWebModules(null);
            return m;
        });
        return ResponseUtil.wrapOrNotFound(module);
    }

    @GetMapping("/modules")
    @Timed
    public List<Module> getWebModules() {
        LOG.debug("Getting all active modules");

        List<Module> providers = new LinkedList<>(moduleRepository.findAllWithProviders());
        providers.addAll(moduleRepository.findAllWithoutProviders());
        providers = providers.stream()
                .map(module -> {
                    List<WebModule> webModules = webModuleRepository.findByModule(module);
                    module.setWebModules(new HashSet<>(webModules));
                    return module;
                }).collect(Collectors.toList());

        return providers;
    }

    @PostMapping("/modules/activate")
    public ModuleResponse activateModule(@RequestBody Module module) {
        return moduleService.activate(module);
    }

    @PostMapping("/modules/deactivate")
    public ModuleResponse deactivateModule(@RequestBody Module module) {
        return moduleService.deactivate(module);
    }

    @PostMapping("/modules/uninstall")
    public ModuleResponse shutdownModule(@RequestBody Module module, Boolean uninstall) {
        return moduleService.uninstall(module, uninstall);
    }

    @PostMapping("/modules/update")
    public ModuleResponse updateModule(@RequestBody Module module) {
        return moduleService.update(module, false);
    }

    @PostMapping("/modules/upload")
    public Module uploadModuleData(@RequestParam("file") MultipartFile file) {
        return moduleService.uploadModuleData(file);
    }

    @PostMapping("/modules/install")
    public ModuleResponse installModule(final @RequestBody Module module, @RequestParam Boolean install) {
        return moduleService.installModule(module, install, false);
    }

    @GetMapping("/modules/menus")
    public List<Menu> getMenus() {
        LOG.debug("Getting all menus for current user");

        List<Menu> menuItems = new ArrayList<>();
        Menu menu = new Menu();
        menu.setName("Dashboard");
        menu.setState("dashboard");
        menu.setType(MenuType.ICON);
        menu.setTooltip("Dashboard");
        menu.setIcon("dashboard");
        menuItems.add(menu);

        menu = new Menu();
        menu.setType(MenuType.SEPARATOR);
        menu.setName("Main Items");
        menuItems.add(menu);

        menu = new Menu();
        menu.setName("Dashboard");
        menu.setState("dashboard");
        menu.setType(MenuType.LINK);
        menu.setTooltip("Dashboard");
        menu.setIcon("dashboard");
        menuItems.add(menu);

        Menu admin = new Menu();
        admin.setName("Administration");
        admin.setState("admin");
        admin.setType(MenuType.DROP_DOWN);
        admin.setIcon("settings");
        admin.getAuthorities().add("ROLE_ADMIN");
        admin.setPosition(100);

        Menu mm = new Menu();
        mm.setName("Modules");
        mm.setPosition(10);
        mm.setState("modules");
        admin.getSubs().add(mm);

        mm = new Menu();
        mm.setName("System Configuration");
        mm.setPosition(11);
        mm.setState("configuration");
        admin.getSubs().add(mm);

        mm = new Menu();
        mm.setName("User Management");
        mm.setPosition(10);
        mm.setState("users");
        admin.getSubs().add(mm);

        mm = new Menu();
        mm.setName("Health Checks");
        mm.setPosition(12);
        mm.setState("health");
        admin.getSubs().add(mm);

        mm = new Menu();
        mm.setName("Application Metrics");
        mm.setPosition(13);
        mm.setState("metrics");
        admin.getSubs().add(mm);

        mm = new Menu();
        mm.setName("Log Configurations");
        mm.setPosition(14);
        mm.setState("logs");
        admin.getSubs().add(mm);

        Set<Module> modules = new HashSet<>(moduleRepository.findByActiveIsTrueAndInErrorIsFalse());
        Set<Menu> menusL1 = new HashSet<>();
        modules.forEach(module -> menusL1.addAll(menuRepository.findByModuleAndLevel(module, MenuLevel.LEVEL_1).stream()
                .map(menu1 -> {
                    Set<Menu> menuL2 = new TreeSet<>(menuRepository.findByLevelAndParentName(MenuLevel.LEVEL_2, menu1.getName()));
                    menuL2 = menuL2.stream()
                            .map(menu2 -> {
                                menu1.setType(MenuType.DROP_DOWN);
                                Set<Menu> menuL3 = new TreeSet<>(menuRepository.findByLevelAndParentName(MenuLevel.LEVEL_3, menu2.getName()));
                                menu2.setSubs(new HashSet<>(menuL3));
                                if (!menuL3.isEmpty()) {
                                    menu2.setType(MenuType.DROP_DOWN);
                                }
                                return menu2;
                            })
                            .sorted(Comparator.comparing(Menu::getName))
                            .collect(Collectors.toCollection(LinkedHashSet::new));
                    if (!menu1.getName().equals("Administration")) {
                        menu1.setSubs(new HashSet<>(menuL2));
                    } else {
                        Set<Menu> adminSub = admin.getSubs();
                        adminSub.addAll(menuL2);
                        admin.setSubs(adminSub);
                    }
                    return menu1;
                })
                .filter(menu1 -> !menu1.getName().equals("Administration"))
                .collect(Collectors.toList())
        ));
        List<Menu> menus = new ArrayList<>(menusL1);
        Collections.sort(menus);
        menuItems.addAll(menus);
        menuItems.add(admin);

        return menuItems.stream().distinct().collect(Collectors.toList());
    }

    @GetMapping("/modules/installed")
    @Cacheable(cacheNames = "modules")
    public List<Module> getModules() {
        LOG.debug("Get all installed modules");
        return moduleRepository.findAll().stream()
                .map(module -> {
                    module.setWebModules(null);
                    return module;
                })
                .collect(Collectors.toList());
    }
}
