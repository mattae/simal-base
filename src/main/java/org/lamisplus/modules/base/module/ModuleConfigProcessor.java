package org.lamisplus.modules.base.module;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foreach.across.modules.user.business.Permission;
import com.foreach.across.modules.user.business.PermissionGroup;
import com.foreach.across.modules.user.business.Role;
import com.foreach.across.modules.user.repositories.RoleRepository;
import com.foreach.across.modules.user.services.PermissionService;
import com.foreach.across.modules.user.services.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.lamisplus.modules.base.business.PermissionProperties;
import org.lamisplus.modules.base.business.PermissionPropertiesService;
import org.lamisplus.modules.base.business.RoleProperties;
import org.lamisplus.modules.base.business.RolePropertiesService;
import org.lamisplus.modules.base.domain.entities.Module;
import org.lamisplus.modules.base.domain.entities.*;
import org.lamisplus.modules.base.domain.repositories.FormRepository;
import org.lamisplus.modules.base.domain.repositories.ModuleRepository;
import org.lamisplus.modules.base.yml.FormElement;
import org.lamisplus.modules.base.yml.ModuleConfig;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModuleConfigProcessor {
    private final static ObjectMapper objectMapper = new ObjectMapper();
    private final ModuleRepository moduleRepository;
    private final FormRepository formRepository;
    private final PermissionService permissionService;
    private final PermissionPropertiesService permissionPropertiesService;
    private final RolePropertiesService rolePropertiesService;
    private final RoleService roleService;
    private final RoleRepository roleRepository;

    @Transactional
    public void deleteModule(Module module) {
        assert module.getId() != null;
        moduleRepository.deleteById(module.getId());
        moduleRepository.flush();
    }

    @Transactional
    public void processConfig(ModuleConfig moduleConfig, Module module) {
        Assert.notNull(moduleConfig, "Module Config cannot be null");
        List<RoleProperties> roleProperties = new ArrayList<>();
        List<PermissionProperties> permissionProperties = new ArrayList<>();
        moduleConfig.getPermissions()
            .forEach(perm -> {
                savePermission(permissionProperties, perm, module);
            });
        moduleConfig.getRoles()
            .forEach(rl -> {
                Role role = roleService.getRole(rl.getAuthority());
                if (role == null) {
                    List<String> permissions = rl.getPermissions().stream()
                        .map(perm -> {
                            savePermission(permissionProperties, perm, module);
                            return perm.getName();
                        }).collect(Collectors.toList());
                    role = roleService.defineRole(rl.getAuthority(), rl.getName(), rl.getDescription(), permissions);
                    RoleProperties properties = rolePropertiesService.getProperties(role.getId());
                    properties.set("moduleId", module.getId());
                    roleProperties.add(properties);
                    roleRepository.flush();
                } else {
                    List<String> permissions = rl.getPermissions().stream()
                        .map(perm -> {
                            savePermission(permissionProperties, perm, module);
                            return perm.getName();
                        }).collect(Collectors.toList());
                    roleService.defineRole(rl.getAuthority(), rl.getName(), rl.getDescription(), permissions);
                }
            });
        permissionProperties.forEach(permissionPropertiesService::saveProperties);
        roleProperties.forEach(rolePropertiesService::saveProperties);

        module.getMenus().clear();
        Set<Menu> menus;
        menus = moduleConfig.getMenus().stream()
            .map(menuItem -> {
                menuItem.setModule(module);
                return menuItem;
            })
            .map(menu -> {
                Set<Menu> subs1 = menu.getSubs();
                subs1 = subs1.stream()
                    .map(sub -> {
                        sub.setModule(menu.getModule());
                        sub.setParent(menu);
                        return sub;
                    })
                    .map(sub -> {
                        Set<Menu> subs2 = sub.getSubs();
                        subs2 = subs2.stream()
                            .map(sub2 -> {
                                sub2.setModule(menu.getModule());
                                sub2.setParent(sub);
                                return sub2;
                            })
                            .collect(Collectors.toSet());
                        sub.getSubs().clear();
                        sub.getSubs().addAll(subs2);
                        return sub;
                    })
                    .collect(Collectors.toSet());
                menu.getSubs().clear();
                menu.getSubs().addAll(subs1);
                return menu;
            })
            .collect(Collectors.toSet());
        module.setMenus(menus);

        module.getWebRemotes().addAll(moduleConfig.getWebRemotes().stream()
            .map(webRemote -> {
                webRemote.setModule(module);
                Set<ExposedComponent> components = webRemote.getComponents().stream()
                    .map(c -> {
                        c.setWebRemote(webRemote);
                        return c;
                    }).collect(Collectors.toSet());
                webRemote.setComponents(components);
                Set<ExposedModule> modules = webRemote.getModules().stream()
                    .map(c -> {
                        c.setWebRemote(webRemote);
                        return c;
                    }).collect(Collectors.toSet());
                webRemote.setModules(modules);
                return webRemote;
            })
            .collect(Collectors.toList()));

        module.getWebComponents().addAll(moduleConfig.getWebComponents().stream()
            .map(webComponent -> {
                webComponent.setModule(module);
                return webComponent;
            }).collect(Collectors.toList()));
    }

    @Transactional
    public void saveJsonForm(Module module, ModuleConfig config) {
        List<FormElement> forms = config.getFormElements();
        String name = StringUtils.stripStart(module.getName(), "#");
        forms.forEach(formElement -> loadJsonForm(module, formElement));
    }

    private void loadJsonForm(Module module, FormElement formElement) {
        try {
            JsonNode json = null;
            if (formElement.getLocation() != null) {
                json = objectMapper.readTree(new ClassPathResource(formElement.getLocation()).getInputStream());
            }
            Form form = new Form();
            form.setName(formElement.getName());
            form.setModule(module);
            form.setFormData(json);
            form.setPriority(formElement.getPriority());
            form.setComponentId(formElement.getComponentId());
            formRepository.save(form);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void savePermission(List<PermissionProperties> permissionProperties, org.lamisplus.modules.base.yml.Permission perm,
                               Module module) {
        String name = StringUtils.stripStart(module.getName(), "#");
        Permission permission = permissionService.definePermission(perm.getName(), perm.getDescription(), name);
        PermissionGroup permissionGroup = permissionService.getPermissionGroup(name);
        permissionGroup.setTitle(String.format("Module: %s", name));
        permissionGroup.setDescription(String.format("Custom permissions defined by the %s module.", name));
        permissionService.saveGroup(permissionGroup);

        PermissionProperties properties = permissionPropertiesService.getProperties(permission.getId());
        properties.set("moduleId", module.getId());
        permissionProperties.add(properties);
    }
}
