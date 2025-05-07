package com.hy.multi_tenant_core.config;

import com.hy.multi_tenant_core.datasource.MultiTenantDataSource;
import com.hy.multi_tenant_core.properties.MultiTenancyProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(MultiTenancyProperties.class)
public class MultiTenancyConfig {

    private final MultiTenancyProperties multiTenancyProperties;

    @Bean
    public DataSource dataSource() {
        Map<Object, Object> resolvedDataSources = new HashMap<>();

        multiTenancyProperties.getTenants().forEach((key, props) -> {
            DataSourceBuilder<?> builder = DataSourceBuilder.create()
                    .url(props.getUrl())
                    .username(props.getUsername())
                    .password(props.getPassword())
                    .driverClassName(props.getDriverClassName());
            resolvedDataSources.put(key, builder.build());
        });

        MultiTenantDataSource dataSource = new MultiTenantDataSource();
        dataSource.setTargetDataSources(resolvedDataSources);
        dataSource.setDefaultTargetDataSource(resolvedDataSources.get("tenant1"));
        return dataSource;
    }
}
