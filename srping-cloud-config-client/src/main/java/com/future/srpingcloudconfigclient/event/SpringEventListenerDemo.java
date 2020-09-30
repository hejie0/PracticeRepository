package com.future.srpingcloudconfigclient.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.support.GenericApplicationContext;

/**
 * srping.jar META-INF/spring.factories
 * 全局搜索 org.springframework.context.ApplicationListener=\
 * Application Listeners 根据Ordered接口控制优先级，数值越小 越优先
 */
public class SpringEventListenerDemo {

    public static void main(String[] args) {
        GenericApplicationContext context = new GenericApplicationContext();
        //添加事件监听器
        context.addApplicationListener(new ApplicationListener<ApplicationEvent>() {
            @Override
            public void onApplicationEvent(ApplicationEvent event) {
                System.err.println("监听事件: " + event);
            }
        });
        context.addApplicationListener(new ClosedListener());

        //启动Spring应用上下文
        //一个是ContextRefreshedEvent
        context.refresh();   //上下文启动时先调用一次publishEvent, refresh -> finishRefresh -> publishEvent

        //一个是PayloadApplicationEvent
        //Spring应用上下文发布事件
        context.publishEvent("HelloWorld");

        //自定义事件, 一个是MyEvent
        context.publishEvent(new MyEvent("hello world"));
        //关闭应用上下文，一个是ContextClosedEvent
        context.close();  //关闭时也会调用publishEvent，close -> doClose -> publishEvent

        /**
         * ContextRefreshedEvent
         *      ApplicationContextEvent
         *          ApplicationEvent
         *
         * ContextClosedEvent
         *      ApplicationContextEvent
         *          ApplicationEvent
         *
         * 自定义事件
         * PayloadApplicationEvent
         *
         * Spring事件都是ApplicationEvent类型
         *
         * 发送Spring事件通过ApplicationEventMulticaster.multicastEvent(ApplicationEvent, ResolvableType)
         *
         * Spring事件类型ApplicationEvent
         * Spring事件监听器ApplicationListener
         * Spring事件广播器ApplicationEventMulticaster
         *      实现类SimpleApplicationEventMulticaster
         *
         * Spring事件理解为消息
         * ApplicationEvent                 相当于消息内容
         * ApplicationListener              相当于消息消费者、订阅者
         * ApplicationEventMulticaster      相当于消息生产者、发布者
         * ApplicationEventPublisher        事件发送器
         */
    }

    //Spring事件都是ApplicationEvent类型
    //ApplicationListener<ApplicationEvent> 泛型可以指定ApplicationEvent的某个子类，对某个事件的具体监听
    private static class ClosedListener implements ApplicationListener<ContextClosedEvent> {
        @Override
        public void onApplicationEvent(ContextClosedEvent event) {
            System.err.println("监听上下文关闭事件: " + event);
        }
    }

    private static class MyEvent extends ApplicationEvent {

        public MyEvent(Object source) {
            super(source);
        }
    }

}
