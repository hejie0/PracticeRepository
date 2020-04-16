package bio.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class BioServerHandler implements Runnable {

    private Socket socket;

    public BioServerHandler(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;

        try{
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            String line = null;
            while((line = in.readLine()) != null){
                System.out.println("Server info: " + line);
                out.write(line.toUpperCase());
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            close(in, out, socket);
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
}
