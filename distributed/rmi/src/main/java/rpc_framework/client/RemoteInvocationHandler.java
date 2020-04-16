package rpc_framework.client;

import rpc_framework.common.RpcRequest;
import rpc_framework.zookeeper.client.IServiceDiscovery;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class RemoteInvocationHandler implements InvocationHandler {

    private IServiceDiscovery serviceDiscovery;
    private String version;

    public RemoteInvocationHandler(IServiceDiscovery serviceDiscovery, String version){
        this.serviceDiscovery = serviceDiscovery;
        this.version = version;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = new RpcRequest();
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameters(args);
        request.setVersion(version);

        String serviceAddress = serviceDiscovery.discovery(request.getClassName()); //根据接口名称，得到对应的服务地址
        TCPTransport tcpTransport = new TCPTransport(serviceAddress);
        return tcpTransport.send(request);
    }
}
