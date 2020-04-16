package rpc_framework;

import rpc_framework.server.RpcServer;
import rpc_framework.service.IUserService;
import rpc_framework.service.UserServiceImpl2;
import rpc_framework.zookeeper.ZkConfig;
import rpc_framework.zookeeper.server.IRegisterCenter;
import rpc_framework.zookeeper.server.RegisterCenterImpl;

public class ServerDemo1 {

    public static void main(String[] args){
        IUserService userService2 = new UserServiceImpl2();
        IRegisterCenter registerCenter = new RegisterCenterImpl(ZkConfig.CONNECT_STR);
        RpcServer server = new RpcServer(registerCenter, "127.0.0.1:8081");
        server.bind(userService2);
        server.publisher();
    }

}
