package bio.simple;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BioServer {

    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        ServerSocket server = null;
        //ThreadPoolExecutor pool = new ThreadPoolExecutor(500, 100, 0, TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>());
        try{
            server = new ServerSocket(PORT);
            System.out.println("the time server is start in port: " + PORT);
            Socket socket = null;
            while(true){
                socket = server.accept();
                new Thread(new TimeServerHandler(socket)).start();
                //pool.submit(new TimeServerHandler(socket)); //使用线程池
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(server != null){
                System.out.println("the time server close");
                server.close();
            }
        }

    }
}
