package com.hy.multi_tenant_core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "multitenancy")
@Data
public class MultiTenancyProperties {

    private Map<String, DataSourceProperties> tenants = new HashMap<>();

    @Data
    public static class DataSourceProperties {
        private String url;
        private String username;
        private String password;
        private String driverClassName;
    }

}
