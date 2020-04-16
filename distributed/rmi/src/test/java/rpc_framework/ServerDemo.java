package rpc_framework;

import rpc_framework.server.RpcServer;
import rpc_framework.service.IUserService;
import rpc_framework.service.UserServiceImpl;
import rpc_framework.zookeeper.ZkConfig;
import rpc_framework.zookeeper.server.IRegisterCenter;
import rpc_framework.zookeeper.server.RegisterCenterImpl;

public class ServerDemo {

    public static void main(String[] args){
        IUserService userService = new UserServiceImpl();
        IRegisterCenter registerCenter = new RegisterCenterImpl(ZkConfig.CONNECT_STR);
        RpcServer server = new RpcServer(registerCenter, "127.0.0.1:8080");
        server.bind(userService);
        server.publisher();
    }

}
