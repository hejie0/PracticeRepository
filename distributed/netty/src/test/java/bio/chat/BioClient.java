package bio.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class BioClient {

    private final String host;
    private final int port;

    public BioClient(String host, int port){
        this.host = host;
        this.port = port;
    }

    public void start(){
        BufferedReader in = null;
        PrintWriter out = null;
        Socket socket = null;
        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("客户端连接成功，host: " + host + " port: " + port);

//            Scanner sysIn = new Scanner(System.in);
//            while (true){
//                String line = sysIn.nextLine();
//                if("bye".equals(line)){  //如果控制台输入bye则退出对话
//                    break;
//                }

                out.println("hello world"); //将控制台输入的，发送给服务端
                String resp = in.readLine(); //接收服务端的响应结果
                System.out.println(resp);
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            close(in, out, socket);
            System.out.println("the time client close");
        }
    }

    public void close(BufferedReader in, PrintWriter out, Socket socket){
        if(in != null){
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(out != null){
            out.close();
        }
        if(socket != null){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args){
        new BioClient("127.0.0.1", 7777).start();
    }
}
