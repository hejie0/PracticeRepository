package rpc_framework.service;

import rpc_framework.annotation.RpcAnnotation;

@RpcAnnotation(IUserService.class)
public class UserServiceImpl implements IUserService {

    @Override
    public String sayHello(String name) {
        return "[I am 8080 port] hello " + name;
    }

}
