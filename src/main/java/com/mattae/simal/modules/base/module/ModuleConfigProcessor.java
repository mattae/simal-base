package com.mattae.simal.modules.base.module;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foreach.across.modules.filemanager.business.reference.FileReference;
import com.foreach.across.modules.filemanager.business.reference.FileReferenceRepository;
import com.foreach.across.modules.filemanager.business.reference.properties.FileReferencePropertiesService;
import com.foreach.across.modules.filemanager.services.FileManager;
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
import com.mattae.simal.modules.base.business.RolePropertiesService;
import com.mattae.simal.modules.base.domain.entities.Module;
import com.mattae.simal.modules.base.domain.entities.*;
import com.mattae.simal.modules.base.domain.repositories.*;
import com.mattae.simal.modules.base.yml.ModuleConfig;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModuleConfigProcessor {
    private final PermissionService permissionService;
    private final PermissionPropertiesService permissionPropertiesService;
    private final RolePropertiesService rolePropertiesService;
    private final MenuRepository menuRepository;
    private final WebRemoteRepository webRemoteRepository;
    private final ExposedComponentRepository exposedComponentRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final RoleRepository roleRepository;
    private final TranslationsRepository translationsRepository;
    private final ConfigurationRepository configurationRepository;
    private final ValueSetRepository valueSetRepository;
    private final FileReferenceRepository fileReferenceRepository;
    private final FileManager fileManager;
    private final FileReferencePropertiesService fileReferencePropertiesService;

    @Transactional
    public void processConfig(Module module) throws IOException {
        ModuleConfig moduleConfig = getConfigFromUrl(urlForModule(module));
        Assert.notNull(moduleConfig, "Module Config cannot be null");

        saveRolesAndPermissions(module, moduleConfig);
        saveMenus(module, moduleConfig);
        saveWebRemotes(module, moduleConfig);
        saveTranslations(module, moduleConfig);
        saveConfigurations(module, moduleConfig);
        saveValueSets(module, moduleConfig);
    }

    @Transactional
    public void deleteRelatedResources(Module module) {
        deleteRolesAndPermissions(module);
        translationsRepository.deleteByModule(module);
        configurationRepository.deleteByModule(module);
        valueSetRepository.deleteByModule(module);
        menuRepository.deleteByModule(module);
        exposedComponentRepository.deleteByWebRemoteModule(module);
        webRemoteRepository.deleteByModule(module);
    }

    private void deleteRolesAndPermissions(Module module) {
        List<PermissionGroup> permissionGroups = new ArrayList<>();
        permissionPropertiesService.getEntityIdsForPropertyValue("moduleId", module.getId())
            .forEach(id -> permissionRepository.findById(id).ifPresent(permission -> {
                permissionPropertiesService.deleteProperties(id);
                PermissionGroup group = permission.getGroup();
                permissionGroups.add(group);
                roleService.getRoles().forEach(role -> {
                    role.getPermissions().remove(permission);
                });
                try {
                    permissionService.deletePermission(permission);
                } catch (Exception ignored) {
                }
            }));
        permissionGroups.forEach(permissionService::deleteGroup);
        rolePropertiesService.getEntityIdsForPropertyValue("moduleId", module.getId())
            .forEach(id -> {
                rolePropertiesService.deleteProperties(id);
                roleRepository.findById(id).ifPresent(role -> {
                    userRepository.findAll()
                        .forEach(user -> {
                            user.getRoles().remove(role);
                        });
                });
                roleRepository.deleteById(id);
            });
    }

    private void saveConfigurations(Module module, ModuleConfig config) {
        if (config.getConfigurationsPath() != null) {
            try {
                URLClassLoader classLoader = new URLClassLoader(new URL[]{urlForModule(module)});
                URL url = classLoader.getResource(config.getConfigurationsPath());
                if (url != null) {
                    List<Configuration> configurations = new ObjectMapper().readValue(url, new TypeReference<>() {
                    });
                    configurations = configurations.stream()
                        .map(configuration -> {
                            configuration.setModule(module);
                            configuration.setId(null);
                            return configuration;
                        })
                        .toList();

                    configurationRepository.saveAll(configurations);
                }
                classLoader.close();
            } catch (Exception ignored) {
            }
        }
    }

    private void saveValueSets(Module module, ModuleConfig config) {

        if (config.getValueSetsPath() != null) {
            try {
                URLClassLoader classLoader = new URLClassLoader(new URL[]{urlForModule(module)});
                URL url = classLoader.getResource(config.getValueSetsPath());
                if (url != null) {
                    List<ValueSet> valueSets = new ObjectMapper().readValue(url, new TypeReference<>() {
                    });
                    valueSets = valueSets.stream()
                        .map(valueSet -> {
                            valueSet.setModule(module);
                            valueSet.setId(null);
                            return valueSet;
                        })
                        .toList();

                    valueSetRepository.saveAll(valueSets);
                }
                classLoader.close();
            } catch (Exception ignored) {
            }
        }
    }

    private void saveTranslations(Module module, ModuleConfig config) {
        translationsRepository.deleteByModule(module);
        if (!config.getTranslations().isEmpty()) {
            config.getTranslations().forEach(tran -> {
                String path = tran.getPath();
                String lang = tran.getLang();
                Translation translation = new Translation();
                translation.setLang(lang);
                translation.setModule(module);
                try {
                    URLClassLoader classLoader = new URLClassLoader(new URL[]{urlForModule(module)});
                    URL url = classLoader.getResource(path);
                    if (url != null) {
                        try {
                            String data = new String(FileCopyUtils.copyToByteArray(url.openConnection().getInputStream()));
                            JsonNode node = new ObjectMapper().readTree(data);
                            translation.setData(node);
                            translationsRepository.save(translation);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    classLoader.close();
                } catch (IOException ignored) {
                }
            });
        }
    }

    private void saveRolesAndPermissions(Module module, ModuleConfig moduleConfig) {
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
    }

    private void saveWebRemotes(Module module, ModuleConfig moduleConfig) {
        List<WebRemote> webRemotes = moduleConfig.getWebRemotes().stream()
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
            }).toList();
        webRemoteRepository.saveAll(webRemotes);
    }

    private void saveMenus(Module module, ModuleConfig moduleConfig) {
        Set<Menu> menus = moduleConfig.getMenus().stream()
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
        menuRepository.saveAll(menus);
    }

    private void savePermission(List<PermissionProperties> permissionProperties, com.mattae.simal.modules.base.yml.Permission perm,
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

    private URL urlForModule(Module module) throws IOException {
        InputStream inputStream = null;
        Path tmp = Files.createTempFile("module", null);
        byte[] data = module.getData();
        if (data != null) {
            inputStream = new ByteArrayInputStream(data);
        } else {
            Collection<Long> ids = fileReferencePropertiesService.getEntityIdsForPropertyValue("name", module.getName());
            if (!ids.isEmpty()) {
                FileReference reference = fileReferenceRepository.getOne(ids.iterator().next());
                inputStream = fileManager.getFileResource(reference.getFileDescriptor()).getInputStream();
            }
        }

        assert inputStream != null;
        IOUtils.copy(inputStream, new FileOutputStream(tmp.toFile()));
        return tmp.toUri().toURL();
    }

    private ModuleConfig getConfigFromUrl(URL url) {
        try {
            return ModuleUtils.loadModuleConfig(new FileInputStream(url.getFile()), "module.yml");
        } catch (Exception e) {
            return null;
        }
    }
}
