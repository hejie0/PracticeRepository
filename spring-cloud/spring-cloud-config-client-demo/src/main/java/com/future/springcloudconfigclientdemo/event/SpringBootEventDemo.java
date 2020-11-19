package com.future.springcloudconfigclientdemo.event;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootEventDemo {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(SpringBootEventDemo.class);
        application.addListeners(event -> {
            System.err.println("监听到事件: " + event.getClass().getSimpleName());
        });
        application.setWebApplicationType(WebApplicationType.SERVLET);
        application.run(args);

        /**
         * 监听到事件: ApplicationStartingEvent
         * 监听到事件: ApplicationEnvironmentPreparedEvent
         * 监听到事件: ApplicationContextInitializedEvent
         * 监听到事件: ApplicationPreparedEvent
         * 监听到事件: ContextRefreshedEvent
         * 监听到事件: ServletWebServerInitializedEvent
         * 监听到事件: ApplicationStartedEvent
         * 监听到事件: ApplicationReadyEvent
         * 监听到事件: ContextClosedEvent
         * 监听到事件: ApplicationFailedEvent (特殊情况)
         * 其他事件: 等。。。
         */
    }

}
