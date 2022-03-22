package com.mattae.simal.modules.base.integration.services;

import com.blazebit.persistence.integration.jackson.EntityViewAwareObjectMapper;
import com.blazebit.persistence.view.EntityViewManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foreach.across.test.AcrossTestConfiguration;
import com.foreach.across.test.AcrossWebAppConfiguration;
import com.mattae.simal.modules.base.BaseModule;
import com.mattae.simal.modules.base.domain.entities.Configuration;
import com.mattae.simal.modules.base.domain.repositories.ConfigurationRepository;
import com.mattae.simal.modules.base.services.ConfigurationService;
import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import io.github.benas.randombeans.randomizers.range.DoubleRangeRandomizer;
import io.github.benas.randombeans.randomizers.range.IntegerRangeRandomizer;
import io.github.benas.randombeans.randomizers.text.StringRandomizer;
import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

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
@Slf4j
public class TestConfigurationService {

    static EnhancedRandom enhancedRandom = EnhancedRandomBuilder
        .aNewEnhancedRandomBuilder()
        .stringLengthRange(10, 20)
        .randomize(Integer.class, IntegerRangeRandomizer.aNewIntegerRangeRandomizer(0, 10))
        .randomize(String.class, StringRandomizer.aNewStringRandomizer(6))
        .randomize(Double.class, DoubleRangeRandomizer.aNewDoubleRangeRandomizer(0.0, 10.0))
        .collectionSizeRange(2, 3)
        .objectPoolSize(30)
        .build();
    @RegisterExtension
    static RandomBeansExtension randomBeansExtension = new RandomBeansExtension(enhancedRandom);
    @Autowired
    ConfigurationService configurationService;
    @Autowired
    ConfigurationRepository configurationRepository;
    @Random(excludes = {"id", "module"})
    Configuration configuration;

    @BeforeEach
    public void setup() throws IOException {
        List<Configuration> configurations =
            new ObjectMapper().readValue(new ClassPathResource("configuration.json").getURL(),
                new TypeReference<>() {
                });
        configurationRepository.saveAll(configurations);
    }

    @Test
    public void testSave() throws IOException {

        LOG.info("Configuration: {}", configurationService.list(null, null));
    }

    @Test
    public void testGetKey() throws IOException {

        LOG.info("Configurations: {}", configurationService.getValueAsNumericForKey("CORE.ADDRESS", "COUNT"));
    }

    @org.springframework.context.annotation.Configuration
    @AcrossTestConfiguration(modules = BaseModule.NAME, modulePackages = "com.mattae.simal.modules", expose = {EntityViewManager.class, EntityManager.class, ObjectMapper.class, EntityViewAwareObjectMapper.class})
    @PropertySource(value = "classpath:across-test.properties")
    static class Config {

    }
}
