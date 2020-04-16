package rpc_framework;

import rpc_framework.client.RpcClientProxy;
import rpc_framework.service.IUserService;
import rpc_framework.zookeeper.ZkConfig;
import rpc_framework.zookeeper.client.IServiceDiscovery;
import rpc_framework.zookeeper.client.ServiceDiscoveryImpl;

public class ClientDemo {

    public static void main(String[] args){
        IServiceDiscovery serviceDiscovery = new ServiceDiscoveryImpl(ZkConfig.CONNECT_STR);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(serviceDiscovery);
        for(int i = 0; i < 10; i++){
            IUserService userService = rpcClientProxy.clientProxy(IUserService.class, null);
            String result = userService.sayHello("rmi");
            System.out.println(result);
        }
    }

}
