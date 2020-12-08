package com.future.springcloudconfigclientdemo.environment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.config.client.ConfigServicePropertySourceLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CustomConfigServiceBootstrapConfiguration {

    @Autowired
    private Environment environment;

    @Bean
    public ConfigServicePropertySourceLocator configServicePropertySourceLocator() {
        ConfigClientProperties clientProperties = configClientProperties();
        ConfigServicePropertySourceLocator configServicePropertySourceLocator = new ConfigServicePropertySourceLocator(clientProperties);
        configServicePropertySourceLocator.setRestTemplate(customRestTemplate(clientProperties));
        return configServicePropertySourceLocator;
    }

    public ConfigClientProperties configClientProperties() {
        ConfigClientProperties client = new ConfigClientProperties(this.environment);
        client.setUri(new String[]{"http://localhost:9090/"});
        client.setName("future");
        client.setProfile("prod");
        client.setLabel("master");
        client.setEnabled(true); //默认是true
        return client;
    }

    /**
     * RestTemplate restTemplate = this.restTemplate == null ? getSecureRestTemplate(properties) : this.restTemplate;
     *
     * @param clientProperties
     * @return
     */
    private RestTemplate customRestTemplate(ConfigClientProperties clientProperties) {
        return null;
    }

}