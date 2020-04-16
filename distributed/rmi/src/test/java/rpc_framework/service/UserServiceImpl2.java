package rpc_framework.service;

import rpc_framework.annotation.RpcAnnotation;

@RpcAnnotation(value = IUserService.class)
public class UserServiceImpl2 implements IUserService {

    @Override
    public String sayHello(String name) {
        return "[I am 8081 port] hello " + name;
    }

}
