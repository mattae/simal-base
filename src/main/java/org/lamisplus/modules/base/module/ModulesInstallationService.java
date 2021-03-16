package org.lamisplus.modules.base.module;

import com.foreach.across.core.AcrossContext;
import com.foreach.across.modules.user.business.PermissionGroup;
import com.foreach.across.modules.user.repositories.PermissionRepository;
import com.foreach.across.modules.user.repositories.RoleRepository;
import com.foreach.across.modules.user.repositories.UserRepository;
import com.foreach.across.modules.user.services.PermissionService;
import com.foreach.across.modules.user.services.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.lamisplus.modules.base.business.PermissionPropertiesService;
import org.lamisplus.modules.base.business.RolePropertiesService;
import org.lamisplus.modules.base.config.ApplicationProperties;
import org.lamisplus.modules.base.domain.entities.Module;
import org.lamisplus.modules.base.domain.repositories.ModuleRepository;
import org.lamisplus.modules.base.yml.ModuleConfig;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModulesInstallationService {
    private final ApplicationProperties applicationProperties;
    private final ModuleConfigProcessor configProcessor;
    private final ModuleRepository moduleRepository;
    private final PermissionRepository permissionRepository;
    private final PermissionService permissionService;
    private final PermissionPropertiesService permissionPropertiesService;
    private final RoleRepository roleRepository;
    private final RolePropertiesService rolePropertiesService;
    private final RoleService roleService;
    private final UserRepository userRepository;
    private final AcrossContext acrossContext;

    @EventListener
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        moduleRepository.findByProcessConfigIsTrue()
            .forEach(module -> {
                final Path moduleRuntimePath = Paths.get(applicationProperties.getModulePath(), "runtime",
                    StringUtils.replace(module.getArtifact(), "\\", "/"));
                ModuleConfig config = null;
                try {
                    config = ModuleUtils.loadModuleConfig(new FileInputStream(moduleRuntimePath.toFile()), "module.yml");
                } catch (Exception ignored) {
                }
                if (config != null) {
                    Module copy = module.copy();
                    configProcessor.deleteModule(module);
                    deleteRolesAndPermissions(module);
                    copy.setProcessConfig(false);
                    copy.setStarted(true);
                    copy = moduleRepository.save(copy);
                    configProcessor.processConfig(config, copy);
                    configProcessor.saveJsonForm(copy, config);
                }
            });
        List<Module> uninstall = moduleRepository.findByUninstallIsTrue();
        uninstall.forEach(this::deleteModule);

        acrossContext.getModules().stream()
            .flatMap(acrossModule -> moduleRepository.findByName(acrossModule.getName()).stream())
            .forEach(module -> {
                module.setStarted(true);
                moduleRepository.save(module);
            });
    }

    private void deleteRolesAndPermissions(Module module) {
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

    public void deleteModule(Module module) {
        moduleRepository.delete(module);
    }
}
