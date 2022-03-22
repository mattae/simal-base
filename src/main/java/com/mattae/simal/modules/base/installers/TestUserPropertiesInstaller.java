package com.mattae.simal.modules.base.installers;

import com.foreach.across.core.annotations.Installer;
import com.foreach.across.core.annotations.InstallerMethod;
import com.foreach.across.core.installers.InstallerPhase;
import com.foreach.across.modules.user.business.Role;
import com.foreach.across.modules.user.business.UserProperties;
import com.foreach.across.modules.user.repositories.UserRepository;
import com.foreach.across.modules.user.services.RoleService;
import com.foreach.across.modules.user.services.UserPropertiesService;
import lombok.RequiredArgsConstructor;

import java.util.Collections;

@Installer(description = "Install test user properties", phase = InstallerPhase.AfterModuleBootstrap)
@RequiredArgsConstructor
public class TestUserPropertiesInstaller {
    private final UserPropertiesService userPropertiesService;
    private final UserRepository userRepository;
    private final RoleService roleService;

    @InstallerMethod
    public void install() {
        Role role = roleService.defineRole("ROLE_USER", "User role", "Common user role", Collections.emptyList());
        userRepository.findByUsername("admin").ifPresent(user -> {
            UserProperties userProperties = userPropertiesService.getProperties(user.getId());
            userProperties.set("organisationId", "df60cbee-e8bc-41b4-bc20-2f7cb8f02646");
            userPropertiesService.saveProperties(userProperties);
            user.addRole(role);
            userRepository.save(user);
        });
    }
}
