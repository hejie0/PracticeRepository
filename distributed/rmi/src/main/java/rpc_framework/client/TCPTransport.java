package rpc_framework.client;

import rpc_framework.common.RpcRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TCPTransport {

    private String serviceAddress;

    public TCPTransport(String serviceAddress){
        this.serviceAddress = serviceAddress;
    }

    public Socket newScoket(){
        System.out.println("创建一个新的连接");
        Socket socket = null;
        try {
            String[] arrs = serviceAddress.split(":");
            socket = new Socket(arrs[0], Integer.parseInt(arrs[1]));
            return socket;
        } catch (Exception e) {
            throw new RuntimeException("连接建立失败");
        }
    }

    public Object send(RpcRequest request){
        Socket socket = null;
        try {
            socket = newScoket();
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(request);
            outputStream.flush();

            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            Object result = inputStream.readObject();

            inputStream.close();
            outputStream.close();
            return result;
        } catch (Exception e) {
            throw new RuntimeException("发起远程调用异常", e);
        }finally {
            if (socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
