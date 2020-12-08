package com.future.springcloudconfigserverdemo.environment;

import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Configuration
public class CustomConfigServiceBootstrapConfiguration {

    /**
     * 验证：当Spring应用上下文没有出现EnvironmentRepository Bean的时候，
     *       那么，默认激活 DefaultRepositoryConfiguration
     * @return
     */
    @Bean
    EnvironmentRepository environmentRepository() {
        return new EnvironmentRepository() {
            @Override
            public Environment findOne(String application, String profile, String label) {
                Environment environment = new Environment("default", profile);
                List<PropertySource> propertySources = environment.getPropertySources();

                Map<String, Object> source = new HashMap<>();
//                source.put("my.name", "何捷");

                try (InputStream dbIn = new FileInputStream("src/main/resources/db.properties")) {
                    Properties dbProperties = new Properties();
                    dbProperties.load(dbIn);
                    String myNameKey = "my.name";
                    source.put(myNameKey, dbProperties.getProperty(myNameKey));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                PropertySource mapPropertySource = new PropertySource("map", source);
                propertySources.add(mapPropertySource);
                return environment;
            }
        };
    }

}
