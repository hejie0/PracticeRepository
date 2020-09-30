package rmi.server;


import rmi.common.HelloServiceImpl;
import rmi.common.IHelloService;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    public static void main(String[] args) {
        try {
            server1();
            server2();
            System.out.println("======= 启动RMI服务成功! =======");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 1、直接使用Registry实现rmi
     * @throws RemoteException
     */
    public static void server1() throws RemoteException {
        // 本地主机上的远程对象注册表Registry的实例,默认端口1099
        Registry registry = LocateRegistry.createRegistry(1099);
        // 创建一个远程对象
        IHelloService hello = new HelloServiceImpl();
        // 把远程对象注册到RMI注册服务器上，并命名为HelloService
        registry.rebind("HelloService", hello);
    }

    /**
     * 2、使用Naming方法实现rmi
     * @throws RemoteException
     */
    public static void server2() throws RemoteException, AlreadyBoundException, MalformedURLException {
        // 本地主机上的远程对象注册表Registry的实例
        LocateRegistry.createRegistry(1100);
        // 创建一个远程对象
        IHelloService hello = new HelloServiceImpl();
        // 把远程对象注册到RMI注册服务器上，并命名为HelloService
        //绑定的URL标准格式为：rmi://host:port/name
        Naming.bind("rmi://localhost:1100/HelloService", hello);
    }
}
