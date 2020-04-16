package rpc_framework.client;

import rpc_framework.zookeeper.client.IServiceDiscovery;

import java.lang.reflect.Proxy;

public class RpcClientProxy {

    private IServiceDiscovery serviceDiscovery;

    public RpcClientProxy(IServiceDiscovery serviceDiscovery){
        this.serviceDiscovery = serviceDiscovery;
    }

    public <T> T clientProxy(final Class<T> interfaceCls, String version){
        return (T) Proxy.newProxyInstance(
                interfaceCls.getClassLoader(),
                new Class[]{interfaceCls},
                new RemoteInvocationHandler(serviceDiscovery, version));
    }

}
