package demo.cluster;

import demo.IHelloService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AppCluster {

    public static void main(String[] args){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/dubbo-cluster.xml");
        IHelloService helloService = (IHelloService) context.getBean("helloService");
        for(int i = 0; i < 10; i++){
            System.out.println(helloService.sayHello("dubbo"));
        }
    }
}
