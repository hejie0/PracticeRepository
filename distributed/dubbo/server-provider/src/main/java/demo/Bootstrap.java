package demo;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class Bootstrap {

    public static void main(String[] args) throws IOException {
         //Main.main(new String[]{"spring", "log4j"}); //启动方式
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring/dubbo-server.xml");
        context.start();
        System.out.println("Provider started.");
        System.in.read(); // press any key to exit
    }
}
