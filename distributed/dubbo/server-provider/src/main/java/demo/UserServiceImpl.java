package demo;

public class UserServiceImpl implements IUserService {
    @Override
    public String protocolDemo(String msg) {
        return "I'am Protocol Demo: " + msg;
    }
}
