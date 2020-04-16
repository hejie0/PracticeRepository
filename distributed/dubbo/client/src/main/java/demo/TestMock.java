package demo;

public class TestMock implements IHelloService {
    @Override
    public String sayHello(String msg) {
        return "系统繁忙: " + msg;
    }
}
