package bio.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {

    private final int port;

    public TCPServer(int port){
        this.port = port;
    }

    public void start(){
        ServerSocket serverSocket = null;
        try{
            serverSocket = new ServerSocket(port);
            System.out.println("服务器启动，端口为: " + port);
            Socket socket = null;
            while(true){
                socket = serverSocket.accept();
                new Thread(new TCPServerHandler(socket)).start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally{
            if(serverSocket != null){
                try {
                    serverSocket.close();
                    System.out.println("the time server close");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args){
        new TCPServer(7777).start();
    }
}
