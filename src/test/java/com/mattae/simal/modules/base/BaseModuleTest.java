package com.mattae.simal.modules.base;

import com.foreach.across.core.AcrossContext;
import com.foreach.across.modules.filemanager.FileManagerModuleSettings;
import com.foreach.across.modules.filemanager.services.FileManager;
import com.foreach.across.test.AcrossTestConfiguration;
import com.foreach.across.test.AcrossWebAppConfiguration;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
@AcrossWebAppConfiguration
@AutoConfigureEmbeddedDatabase(beanName = "acrossDataSource")
@TestExecutionListeners({
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class
})
@DbUnitConfiguration(databaseConnection = "acrossDataSource")
@Slf4j
public class BaseModuleTest {
    @Autowired
    AcrossContext context;

    @Autowired
    FileManagerModuleSettings fileManagerModuleSettings;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    FileManager fileManager;

    @Test
    public void module_repository_should_exists() throws Exception {
        mockMvc.perform(get("/api/administrative-divisions/parent/9"))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    public void contextShouldContainBaseModule() {
        assertNotNull(context.getModule(BaseModule.NAME));
    }

    @AcrossTestConfiguration(modules = BaseModule.NAME, expose = {FileManagerModuleSettings.class})
    @PropertySource("classpath:across-test.properties")
    @EnableTransactionManagement
    static class Config {
    }
}
