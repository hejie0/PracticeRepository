package rpc_framework.server;

import rpc_framework.annotation.RpcAnnotation;
import rpc_framework.zookeeper.server.IRegisterCenter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RpcServer {

    //创建线程池
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    //注册中心
    private IRegisterCenter registerCenter;
    //服务发布地址
    private String serviceAddress;
    //绑定服务名称和服务对象关系
    private Map<String, Object> handlerMap = new HashMap<>();

    public RpcServer(IRegisterCenter registerCenter, String serviceAddress){
        this.registerCenter = registerCenter;
        this.serviceAddress = serviceAddress;
    }

    /**
     * 绑定服务名称和服务对象
     * @param services
     */
    public void bind(Object... services){
        for(Object service : services){
            RpcAnnotation annotation = service.getClass().getAnnotation(RpcAnnotation.class);
            String serviceName = annotation.value().getName();
            String version = annotation.version();
            if(version != null && !"".equals(version)){
                serviceName = serviceName + "-" + version;
            }
            handlerMap.put(serviceName, service); //绑定服务接口名称对应的服务
        }
    }

    public void publisher(){
        ServerSocket serverSocket = null;
        try{
            String[] address = serviceAddress.split(":");
            int port = Integer.parseInt(address[1]);
            serverSocket = new ServerSocket(port);

            for (String interfaceName : handlerMap.keySet()){
                registerCenter.register(interfaceName, serviceAddress);
            }

            while (true){
                Socket socket = serverSocket.accept();
                executorService.submit(new ProcessorHandler(socket, handlerMap));
            }
        }catch (Exception e){
            if (serverSocket != null){
                try {
                    serverSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

}
