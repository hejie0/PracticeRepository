package demo;

public class HelloServiceImpl2 implements IHelloService {
    @Override
    public String sayHello(String msg) {
        return "hello2222 " + msg;
    }
}
