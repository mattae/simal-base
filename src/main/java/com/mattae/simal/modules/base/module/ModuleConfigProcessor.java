package com.mattae.simal.modules.base.module;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foreach.across.modules.user.business.Permission;
import com.foreach.across.modules.user.business.PermissionGroup;
import com.foreach.across.modules.user.business.Role;
import com.foreach.across.modules.user.repositories.PermissionRepository;
import com.foreach.across.modules.user.repositories.RoleRepository;
import com.foreach.across.modules.user.repositories.UserRepository;
import com.foreach.across.modules.user.services.PermissionService;
import com.foreach.across.modules.user.services.RoleService;
import com.mattae.simal.modules.base.business.PermissionProperties;
import com.mattae.simal.modules.base.business.PermissionPropertiesService;
import com.mattae.simal.modules.base.business.RoleProperties;
import com.mattae.simal.modules.base.domain.entities.*;
import com.mattae.simal.modules.base.domain.entities.Module;
import com.mattae.simal.modules.base.domain.repositories.MenuRepository;
import com.mattae.simal.modules.base.domain.repositories.TranslationsRepository;
import com.mattae.simal.modules.base.domain.repositories.WebComponentRepository;
import com.mattae.simal.modules.base.domain.repositories.WebRemoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import com.mattae.simal.modules.base.business.RolePropertiesService;
import com.mattae.simal.modules.base.yml.ModuleConfig;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModuleConfigProcessor {
    private final PermissionService permissionService;
    private final PermissionPropertiesService permissionPropertiesService;
    private final RolePropertiesService rolePropertiesService;
    private final WebComponentRepository webComponentRepository;
    private final MenuRepository menuRepository;
    private final WebRemoteRepository webRemoteRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final RoleRepository roleRepository;
    private final TranslationsRepository translationsRepository;

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

        saveTranslations(module, moduleConfig);
    }

    public void savePermission(List<PermissionProperties> permissionProperties, com.mattae.simal.modules.base.yml.Permission perm,
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

    @Transactional
    public void deleteRelationships(Module module) {
        translationsRepository.deleteByModule(module);
        deleteRolesAndPermissions(module);
        webComponentRepository.deleteAll(webComponentRepository.findByModule(module));
        menuRepository.deleteAll(menuRepository.findByModule(module));
        webRemoteRepository.deleteAll(webRemoteRepository.findByModule(module));
        webRemoteRepository.flush();
    }

    @Transactional
    public void deleteRolesAndPermissions(Module module) {
        List<PermissionGroup> permissionGroups = new ArrayList<>();
        permissionPropertiesService.getEntityIdsForPropertyValue("moduleId", module.getId())
            .forEach(id -> {
                permissionRepository.findById(id).ifPresent(permission -> {
                    permissionPropertiesService.deleteProperties(id);
                    PermissionGroup group = permission.getGroup();
                    permissionGroups.add(group);
                    roleService.getRoles().forEach(role -> {
                        role.getPermissions().remove(permission);
                        roleService.save(role);
                    });
                    try {
                        permissionService.deletePermission(permission);
                    } catch (Exception ignored) {
                    }
                });
            });
        permissionGroups.forEach(permissionService::deleteGroup);
        rolePropertiesService.getEntityIdsForPropertyValue("moduleId", module.getId())
            .forEach(id -> {
                rolePropertiesService.deleteProperties(id);
                roleRepository.findById(id).ifPresent(role -> {
                    userRepository.findAll()
                        .forEach(user -> {
                            user.getRoles().remove(role);
                            userRepository.save(user);
                        });
                });
                roleRepository.deleteById(id);
            });
    }

    private void saveTranslations(Module module, ModuleConfig config) {
        if (config.getTranslation() != null) {
            String path = config.getTranslation().getPath();
            String lang = config.getTranslation().getLang();
            Translation translation = new Translation();
            translation.setLang(lang);
            translation.setModule(module);
            Resource resource = new ClassPathResource(path);
            if (resource.isFile()) {
                try {
                    String data = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()));
                    JsonNode node = new ObjectMapper().readTree(data);
                    translation.setData(node);
                    translationsRepository.save(translation);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
