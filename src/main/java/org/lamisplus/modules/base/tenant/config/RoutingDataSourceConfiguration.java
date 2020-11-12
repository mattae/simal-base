package org.lamisplus.modules.base.tenant.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.base.tenant.util.TenantContextHolder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;

@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnBean(DatabaseProperties.class)
public class RoutingDataSourceConfiguration {
    private static RoutingDataSource routingDataSource;
    private final DatabaseProperties properties;

    public class RoutingDataSource extends AbstractRoutingDataSource {

        public RoutingDataSource() {
        }

        @Override
        protected Object determineCurrentLookupKey() {
            return TenantContextHolder.getTenant();
        }
    }

    //@Bean
    public DataSource acrossDataSource() {
        if (routingDataSource != null) {
            DataSource dataSource = DataSourceBuilder.create()
                    .type(HikariDataSource.class)
                    .username(properties.getUsername())
                    .password(properties.getPassword())
                    .url(properties.getUrl())
                    .driverClassName(properties.getDriverClassName())
                    .build();

            RoutingDataSource routingDataSource = new RoutingDataSource();
            routingDataSource.setTargetDataSources(new HashMap<>());
            routingDataSource.setDefaultTargetDataSource(dataSource);
        }
        return routingDataSource;
    }
}
