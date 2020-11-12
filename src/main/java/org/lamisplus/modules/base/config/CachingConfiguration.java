package org.lamisplus.modules.base.config;

import com.foreach.across.core.annotations.Exposed;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.lamisplus.modules.base.tenant.util.TenantContextHolder;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

import static java.util.Arrays.asList;


@Configuration
@EnableCaching
public class CachingConfiguration extends CachingConfigurerSupport {

    @Override
    public KeyGenerator keyGenerator() {
        return new EnvironmentAwareCacheKeyGenerator();
    }

    @Override
    @Bean
    @Primary
    @Exposed
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(200)
                .maximumSize(500)
                .weakKeys()
                .recordStats());
        return cacheManager;
    }

    public class EnvironmentAwareCacheKeyGenerator implements KeyGenerator {

        @Override
        public Object generate(Object target, Method method, Object... params) {

            String key = TenantContextHolder.getTenant() + "-" + (method.getName() + "-") + StringUtils
                    .collectionToDelimitedString(asList(params), "-");
            return key;
        }
    }
}
