package com.future.springcloudconfigclientdemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SpringBootApplication
@EnableScheduling
public class SpringCloudConfigClientDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudConfigClientDemoApplication.class, args);
    }

    @Configuration
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public static class MyPropertySource implements PropertySourceLocator {

        @Override
        public PropertySource<?> locate(Environment environment) {
            Map<String, Object> source = new HashMap<>();
            source.put("server.port", 8080);
            MapPropertySource propertySource = new MapPropertySource("my-property-source", source);
            return propertySource;
        }
    }

    @Autowired
    private Environment environment;
    @Autowired
    private ContextRefresher contextRefresher;

    @Scheduled(fixedRate = 15 * 1000, initialDelay = 3 * 1000)
    public void autoRefresh() {
        Set<String> updatePropertyNames = contextRefresher.refresh();

        updatePropertyNames.forEach( propertyName -> System.out.printf("[Thread: %s] 当前配置已更新：" +
                "具体 Key: %s , Values : %s \n",
                Thread.currentThread().getName(),
                propertyName,
                environment.getProperty(propertyName)));

        if (!updatePropertyNames.isEmpty()) {
			System.out.printf("[Thread: %s] 当前配置已更新，具体项目：%s \n",
					Thread.currentThread().getName(),
					updatePropertyNames);
        }
    }
}
