package bio.chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BioServer {

    private final int port;

    public BioServer(int port){
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
                new Thread(new BioServerHandler(socket)).start();
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
        new BioServer(7777).start();
    }
}
