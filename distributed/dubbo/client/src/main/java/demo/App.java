package demo;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {

    public static void main(String[] args){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/dubbo-client.xml");
        IHelloService helloService = (IHelloService) context.getBean("helloService"); //helloService == proxy0
        IUserService userService = (IUserService) context.getBean("userService");
        System.out.println(helloService.sayHello("dubbo"));
        System.out.println(userService.protocolDemo("hessian"));
    }
}
