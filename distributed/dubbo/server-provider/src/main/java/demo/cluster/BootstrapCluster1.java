package demo.cluster;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class BootstrapCluster1 {

    public static void main(String[] args) throws IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring/dubbo-cluster1.xml");
        context.start();
        System.out.println("Provider started.");
        System.in.read(); // press any key to exit
    }
}
