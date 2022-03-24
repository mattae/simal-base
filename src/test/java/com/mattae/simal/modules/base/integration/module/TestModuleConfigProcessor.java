package com.mattae.simal.modules.base.integration.module;

import com.blazebit.persistence.integration.jackson.EntityViewAwareObjectMapper;
import com.blazebit.persistence.view.EntityViewManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foreach.across.modules.user.business.Role;
import com.foreach.across.modules.user.repositories.PermissionRepository;
import com.foreach.across.modules.user.repositories.RoleRepository;
import com.foreach.across.modules.user.repositories.UserRepository;
import com.foreach.across.modules.user.services.PermissionService;
import com.foreach.across.modules.user.services.RoleService;
import com.foreach.across.test.AcrossTestConfiguration;
import com.foreach.across.test.AcrossWebAppConfiguration;
import com.mattae.simal.modules.base.BaseModule;
import com.mattae.simal.modules.base.business.PermissionPropertiesService;
import com.mattae.simal.modules.base.business.RolePropertiesService;
import com.mattae.simal.modules.base.domain.entities.Module;
import com.mattae.simal.modules.base.domain.repositories.*;
import com.mattae.simal.modules.base.module.ModuleConfigProcessor;
import com.mattae.simal.modules.base.module.ModuleUtils;
import com.mattae.simal.modules.base.services.ModuleService;
import com.mattae.simal.modules.base.web.vm.ModuleVM;
import com.mattae.simal.modules.base.yml.ModuleConfig;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({SpringExtension.class})
@DirtiesContext
@ContextConfiguration
@AcrossWebAppConfiguration
@AutoConfigureEmbeddedDatabase(beanName = "acrossDataSource", provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY)
@TestExecutionListeners({
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class
})
public class TestModuleConfigProcessor {
    @Autowired
    ModuleRepository moduleRepository;
    @Autowired
    TranslationsRepository translationsRepository;
    @Autowired
    ConfigurationRepository configurationRepository;
    @Autowired
    ValueSetRepository valueSetRepository;
    @Autowired
    MenuRepository menuRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    RoleService roleService;
    @Autowired
    PermissionRepository permissionRepository;
    @Autowired
    PermissionService permissionService;
    @Autowired
    PermissionPropertiesService permissionPropertiesService;
    @Autowired
    RolePropertiesService rolePropertiesService;
    @Autowired
    private ModuleConfigProcessor configProcessor;
    @Autowired
    ModuleService moduleService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    WebRemoteRepository webRemoteRepository;
    @Autowired
    ExposedComponentRepository exposedComponentRepository;

    Module module;
    ModuleConfig config;

    @BeforeEach
    @Transactional
    public void setup() throws Exception {
        MultipartFile file = new MockMultipartFile("file", "test", null,
            IOUtils.toByteArray(new ClassPathResource("test-1.0.0.jar").getURL()));
        module = moduleService.uploadModuleData(file);
        ModuleVM vm = moduleService.installOrUpdate(module);
        config = ModuleUtils.loadModuleConfig(file.getInputStream(), "module.yml");
        module = moduleRepository.getOne(vm.getId());
        translationsRepository.deleteAll();
        valueSetRepository.deleteAll();
        configurationRepository.deleteAll();
        translationsRepository.deleteAll();
        userRepository.findAll().forEach(user -> {
            user.getRoles().clear();
        });

        roleRepository.findAll().forEach(role -> {
            role.getPermissions().clear();
            roleRepository.delete(role);
        });
        permissionRepository.deleteAll();
    }

    @AfterEach
    public void teardown() {
        moduleRepository.deleteAll();
    }

    @Test
    @Transactional
    public void testProcessConfigWithoutRelatedResources() {
        ModuleConfig moduleConfig = new ModuleConfig();
        BeanUtils.copyProperties(moduleConfig, config, "name", "basePackage", "version");

        configProcessor.processConfig(module, config);

        assertAll("Should without related resources",
            () -> assertEquals(0, translationsRepository.count()),
            () -> assertEquals(0, configurationRepository.count()),
            () -> assertEquals(0, valueSetRepository.count()),
            () -> assertEquals(1, moduleRepository.count()),
            () -> assertEquals(0, roleRepository.count()),
            () -> assertEquals(0, permissionPropertiesService.getEntityIdsForPropertyValue("moduleId",
                module.getId()).size()),
            () -> assertEquals(0, rolePropertiesService.getEntityIdsForPropertyValue("moduleId",
                module.getId()).size()),
            () -> assertEquals(0, roleRepository.count()),
            () -> assertEquals(0, permissionRepository.count()),
            () -> assertEquals(0, exposedComponentRepository.count()),
            () -> assertEquals(0, webRemoteRepository.count())
        );
    }

    @Test
    @Transactional
    public void testProcessConfigWithRelatedResources() {
        configProcessor.processConfig(module, config);

        assertAll("Should save all related resources",
            () -> assertNotEquals(0, translationsRepository.count()),
            () -> assertNotEquals(0, configurationRepository.count()),
            () -> assertNotEquals(0, valueSetRepository.count()),
            () -> assertNotEquals(0, moduleRepository.count()),
            () -> assertNotEquals(0, roleRepository.count()),
            () -> assertNotEquals(0, permissionPropertiesService.getEntityIdsForPropertyValue("moduleId",
                module.getId()).size()),
            () -> assertNotEquals(0, rolePropertiesService.getEntityIdsForPropertyValue("moduleId",
                module.getId()).size()),
            () -> assertNotEquals(0, roleRepository.count()),
            () -> assertNotEquals(0, permissionRepository.count()),
            () -> assertNotEquals(0, exposedComponentRepository.count()),
            () -> assertNotEquals(0, webRemoteRepository.count())
        );
    }

    @Test
    @Transactional
    public void testDeleteRelatedResources() {
        configProcessor.processConfig(module, config);
        userRepository.findByUsername("admin").ifPresent(user -> {
            Role role = roleService.getRole("ROLE_TEST");
            user.getRoles().add(role);
        });
        configProcessor.deleteRelatedResources(module);
        assertAll("Should delete related resources",
            () -> assertEquals(0, translationsRepository.count()),
            () -> assertEquals(0, configurationRepository.count()),
            () -> assertEquals(0, valueSetRepository.count()),
            () -> assertEquals(0, roleRepository.count()),
            () -> assertEquals(0, permissionPropertiesService.getEntityIdsForPropertyValue("moduleId",
                module.getId()).size()),
            () -> assertEquals(0, rolePropertiesService.getEntityIdsForPropertyValue("moduleId",
                module.getId()).size()),
            () -> assertEquals(0, roleRepository.count()),
            () -> assertEquals(0, permissionRepository.count()),
            () -> assertEquals(0, exposedComponentRepository.count()),
            () -> assertEquals(0, webRemoteRepository.count())
        );
    }


    @Configuration
    @AcrossTestConfiguration(modules = BaseModule.NAME, modulePackages = "com.mattae.simal.modules", expose = {EntityViewManager.class, EntityManager.class, ObjectMapper.class, EntityViewAwareObjectMapper.class})
    @PropertySource(value = "classpath:across-test.properties")
    static class Config {

    }
}
