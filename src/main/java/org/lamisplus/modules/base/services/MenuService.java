package org.lamisplus.modules.base.services;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.lamisplus.modules.base.domain.entities.Menu;
import org.lamisplus.modules.base.domain.entities.Module;
import org.lamisplus.modules.base.domain.enumeration.MenuLevel;
import org.lamisplus.modules.base.domain.enumeration.MenuType;
import org.lamisplus.modules.base.domain.repositories.MenuRepository;
import org.lamisplus.modules.base.domain.repositories.ModuleRepository;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final ModuleRepository moduleRepository;
    private final MenuRepository menuRepository;

    @PostFilter("filterObject.authorities.size() > 0 ? hasAnyAuthority(filterObject.authorities) : true")
    public List<Menu> getMenu() {
        List<Menu> menuItems = new ArrayList<>();
        Menu menu = new Menu();
        menu.setName("Dashboard");
        menu.setState("dashboard");
        menu.setType(MenuType.icon);
        menu.setTooltip("Dashboard");
        menu.setIcon("dashboard");
        menu.setPosition(Integer.MIN_VALUE);
        menuItems.add(menu);

        menu = new Menu();
        menu.setType(MenuType.separator);
        menu.setName("Main Items");
        menu.setPosition(Integer.MIN_VALUE + 1);
        menuItems.add(menu);

        menu = new Menu();
        menu.setName("Dashboard");
        menu.setState("dashboard");
        menu.setType(MenuType.link);
        menu.setTooltip("Dashboard");
        menu.setIcon("dashboard");
        menu.setPosition(Integer.MIN_VALUE + 2);
        menuItems.add(menu);

        Menu admin = new Menu();
        admin.setName("Administration");
        admin.setState("admin");
        admin.setType(MenuType.dropDown);
        admin.setIcon("settings");
        admin.getAuthorities().add("ROLE_ADMIN");
        admin.setPosition(Integer.MAX_VALUE);

        Menu mm = new Menu();
        mm.setName("Modules");
        mm.setPosition(10);
        mm.setState("modules");
        admin.getSubs().add(mm);

        Set<Module> modules = new HashSet<>(moduleRepository.findByActiveIsTrueAndStartedIsTrue());
        Set<Menu> menusL1 = new HashSet<>();
        modules.forEach(module -> menusL1.addAll(menuRepository.findByModuleAndLevel(module, MenuLevel.LEVEL_1).stream()
            .map(menu1 -> {
                Set<Menu> menuL2 = new TreeSet<>(menuRepository.findByLevelAndParentName(MenuLevel.LEVEL_2, menu1.getName()));
                menuL2 = menuL2.stream()
                    .filter(m -> m.getModule().getStarted())
                    .map(menu2 -> {
                        if (StringUtils.isEmpty(menu2.getParent().getState())) {
                            menu2.setIgnoreParentState(true);
                        }
                        menu1.setType(MenuType.dropDown);
                        Set<Menu> menuL3 = new TreeSet<>(menuRepository.findByLevelAndParentName(MenuLevel.LEVEL_3, menu2.getName()))
                            .stream()
                            .filter(m -> m.getModule().getStarted())
                            .map(menu3 -> {
                                if (StringUtils.isEmpty(menu3.getParent().getState())) {
                                    menu3.setIgnoreParentState(true);
                                }
                                return menu3;
                            })
                            .collect(Collectors.toSet());
                        menu2.setSubs(new HashSet<>(menuL3));
                        if (!menuL3.isEmpty()) {
                            menu2.setType(MenuType.dropDown);
                        }
                        return menu2;
                    })
                    .collect(Collectors.toSet());
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
        menuItems.addAll(menus);
        menuItems.add(admin);

        return menuItems;
    }
}
