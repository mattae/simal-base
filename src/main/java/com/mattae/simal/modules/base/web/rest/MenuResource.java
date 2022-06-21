package com.mattae.simal.modules.base.web.rest;

import com.mattae.simal.modules.base.domain.entities.Menu;
import com.mattae.simal.modules.base.services.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MenuResource {
    private final MenuService menuService;

    @GetMapping("/api/menus")
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<Menu> getMenus() {
        return menuService.getMenu();
    }
}
