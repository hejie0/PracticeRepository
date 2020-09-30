package rmi.client;


import rmi.common.IHelloService;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    public static void main(String[] args) {
        try {
            client1();
            client2();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void client1() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(1099);
        IHelloService hello = (IHelloService) registry.lookup("HelloService");
        String response = hello.sayHello("rmi");
        System.out.println("=======> " + response + " <=======");
    }

    public static void client2() throws RemoteException, NotBoundException, MalformedURLException {
        String remoteAddr="rmi://localhost:1100/HelloService";
        IHelloService hello = (IHelloService) Naming.lookup(remoteAddr);
        String response = hello.sayHello("rmi");
        System.out.println("=======> " + response + " <=======");
    }
}
