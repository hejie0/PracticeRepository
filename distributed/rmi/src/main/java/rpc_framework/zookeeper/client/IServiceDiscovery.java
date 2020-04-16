package rpc_framework.zookeeper.client;

public interface IServiceDiscovery {

    /**
     * 根据请求的地址，获得对应的调用地址
     * @param serviceName
     * @return
     */
    String discovery(String serviceName);

}
