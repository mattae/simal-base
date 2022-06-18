package com.mattae.simal.modules.base.integration.services;

import com.foreach.across.modules.filemanager.business.reference.FileReferenceRepository;
import com.foreach.across.modules.filemanager.business.reference.properties.FileReferencePropertiesRepository;
import com.foreach.across.modules.filemanager.business.reference.properties.FileReferencePropertiesService;
import com.foreach.across.test.AcrossTestConfiguration;
import com.foreach.across.test.AcrossWebAppConfiguration;
import com.mattae.simal.modules.base.BaseModule;
import com.mattae.simal.modules.base.domain.entities.Module;
import com.mattae.simal.modules.base.domain.repositories.ModuleRepository;
import com.mattae.simal.modules.base.services.ModuleService;
import com.mattae.simal.modules.base.services.dto.ModuleDependencyDTO;
import com.mattae.simal.modules.base.web.vm.ModuleVM;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.List;

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
public class TestModuleService {
    @Autowired
    ModuleService moduleService;
    @Autowired
    FileReferenceRepository fileReferenceRepository;
    @Autowired
    FileReferencePropertiesService fileReferencePropertiesService;
    @Autowired
    FileReferencePropertiesRepository fileReferencePropertiesRepository;
    @Autowired
    ModuleRepository moduleRepository;

    @BeforeEach
    public void setup() {
        moduleRepository.deleteAll();
        fileReferencePropertiesRepository.getEntityIdsForPropertyValue("name", "TestModule")
            .forEach(id -> fileReferencePropertiesService.deleteProperties(id));
        fileReferenceRepository.deleteAll();
    }

    @Test
    public void testUploadModuleData() throws Exception {
        MultipartFile file = new MockMultipartFile("file", "test", null,
            IOUtils.toByteArray(new ClassPathResource("test-1.0.0.jar").getURL()));

        Module module = moduleService.uploadModuleData(file);
        assertEquals("TestModule", module.getName());
        assertEquals("com.mattae.simal.modules", module.getBasePackage());
        assertEquals("1.0.0", module.getVersion());

        assertEquals(1, fileReferencePropertiesService.getEntityIdsForPropertyValue("name", module.getName()).size());
    }

    @Test
    @Transactional
    public void testInstallOrUpdate() throws Exception {
        MultipartFile file = new MockMultipartFile("file", "test", null,
            IOUtils.toByteArray(new ClassPathResource("test-1.0.0.jar").getURL()));
        Module module = moduleService.uploadModuleData(file);
        ModuleVM vm = moduleService.installOrUpdate(module);

        assertAll("Should complete module attributes",
            () -> {
                assertNotNull(vm.getId());
                assertEquals("TestModule", vm.getName());
                assertEquals("Matthew Edor", vm.getAuthor());
                assertNotNull(vm.getEmail());
                assertNotNull(vm.getImage());
                assertNotNull(vm.getUrl());
                assertNotNull(vm.getDescription());
                assertNotNull(vm.getBuildDate());
                assertNotNull(vm.getBuildTime());
                assertNotNull(vm.getBasePackage());
                assertNotNull(vm.getVersion());
            });
        assertEquals(0, fileReferencePropertiesService.getEntityIdsForPropertyValue("name", module.getName()).size());
        module = moduleRepository.getOne(vm.getId());
        assertNotNull(module.getData());
        assertNull(module.getFile());
    }

    @Test
    @Transactional
    public void testInstallOrUpdateNonStore() throws Exception {
        MultipartFile file = new MockMultipartFile("file", "test", null,
            IOUtils.toByteArray(new ClassPathResource("test-non-store-1.0.0.jar").getURL()));
        Module module = moduleService.uploadModuleData(file);
        ModuleVM vm = moduleService.installOrUpdate(module);

        assertEquals(1, fileReferencePropertiesService.getEntityIdsForPropertyValue("name", module.getName()).size());
        module = moduleRepository.getOne(vm.getId());
        assertNull(module.getData());
        assertNotNull(module.getFile());
        assertEquals(1, fileReferenceRepository.count());
    }

    @Test
    public void testGetDependencies() throws Exception {
        MultipartFile file = new MockMultipartFile("file", "test", null,
            IOUtils.toByteArray(new ClassPathResource("test-1.0.0.jar").getURL()));
        Module module = moduleService.uploadModuleData(file);
        ModuleVM vm = moduleService.installOrUpdate(module);

        List<ModuleDependencyDTO> dependencies = moduleService.getDependencies(vm.getId());
        assertEquals(1, dependencies.size());
        assertEquals("DepModule", dependencies.get(0).getName());
    }

    @Test
    public void testGetDependents() throws Exception {
        MultipartFile file = new MockMultipartFile("file", "test", null,
            IOUtils.toByteArray(new ClassPathResource("test-1.0.0.jar").getURL()));
        Module module = moduleService.uploadModuleData(file);
        moduleService.installOrUpdate(module);

        file = new MockMultipartFile("file", "test", null,
            IOUtils.toByteArray(new ClassPathResource("dep-1.0.0.jar").getURL()));
        module = moduleService.uploadModuleData(file);
        ModuleVM vm = moduleService.installOrUpdate(module);
        List<ModuleDependencyDTO> dependencies = moduleService.getDependents(vm.getId());

        assertEquals(1, dependencies.size());
        assertEquals("TestModule", dependencies.get(0).getName());
    }

    @Test
    @Transactional
    public void testActivate() throws Exception {
        MultipartFile file = new MockMultipartFile("file", "test", null,
            IOUtils.toByteArray(new ClassPathResource("test-1.0.0.jar").getURL()));
        Module module = moduleService.uploadModuleData(file);
        ModuleVM vm = moduleService.installOrUpdate(module);
        moduleRepository.findById(vm.getId()).ifPresent(m -> m.setActive(false));

        assertFalse(moduleRepository.getOne(module.getId()).getActive());
        BeanUtils.copyProperties(vm, module);
        moduleService.activate(module);
        assertTrue(moduleRepository.getOne(module.getId()).getActive());
    }

    @Test
    @Transactional
    public void testDeactivate() throws Exception {
        MultipartFile file = new MockMultipartFile("file", "test", null,
            IOUtils.toByteArray(new ClassPathResource("test-1.0.0.jar").getURL()));
        Module module = moduleService.uploadModuleData(file);
        ModuleVM vm = moduleService.installOrUpdate(module);

        BeanUtils.copyProperties(vm, module);
        moduleService.deactivate(module);
        assertFalse(moduleRepository.getOne(module.getId()).getActive());
    }

    @org.springframework.context.annotation.Configuration
    @AcrossTestConfiguration(modules = BaseModule.NAME, modulePackages = "com.mattae.simal.modules",
        expose = {FileReferencePropertiesRepository.class})
    @PropertySource(value = "classpath:across-test.properties")
    static class Config {

    }
}
