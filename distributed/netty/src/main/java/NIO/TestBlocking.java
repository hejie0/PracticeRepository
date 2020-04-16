package NIO;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 一、使用NIO 完成网络通信的3个核心
 *
 * 1、通道(Channel)：负责连接
 *      java.nio.channels.Channel 接口
 *          |SelectableChannel
 *              |SocketChannel
 *              |ServerSocketChannel
 *              |DatagramChannel
 *
 *              |Pipe.SinkChannel
 *              |Pipe.SourceChannel
 * 2、缓冲区(Buffer)：负责数据存取
 *
 * 3、选择器(Selector)：是SelectableChannel的多路复用器，用于监控SelectableChannel的IO状况
 */
public class TestBlocking {

    @Test
    public void clientBlocking1() throws IOException {
        //1、获取通道
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8888));
        FileChannel fileChannel = FileChannel.open(Paths.get("src/main/java/NIO/image/通道1.png"), StandardOpenOption.READ);
        //2、分配指定大小的缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        //3、读取本地文件发送给服务端
        while (fileChannel.read(byteBuffer) != -1){
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
            byteBuffer.clear();
        }
        //4、关闭通道
        socketChannel.close();
        fileChannel.close();
    }

    @Test
    public void serverBlocking1() throws IOException {
        //1、获取通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        FileChannel fileChannel = FileChannel.open(Paths.get("通道1-副本.png"),StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        //2、绑定端口
        serverSocketChannel.bind(new InetSocketAddress(8888));
        //3、获取客户端连接
        SocketChannel socketChannel = serverSocketChannel.accept();
        //4、分配指定大小的缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        //5、读取客户端传来的文件,保存到本地
        while (socketChannel.read(byteBuffer) != -1){
            byteBuffer.flip();
            fileChannel.write(byteBuffer);
            byteBuffer.clear();
        }
        //6、关闭通道
        serverSocketChannel.close();
        fileChannel.close();
        socketChannel.close();
    }


    @Test
    public void clientBlocking2() throws IOException {
        //1、获取通道
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8888));
        FileChannel fileChannel = FileChannel.open(Paths.get("src/main/java/NIO/image/通道1.png"), StandardOpenOption.READ);
        //2、分配指定大小的缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        //3、读取本地文件发送给服务端
        while (fileChannel.read(byteBuffer) != -1){
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
            byteBuffer.clear();
        }

        //4、关闭对服务端的写入
        socketChannel.shutdownOutput();

        //5、接收服务端的反馈
        int len = 0;
        while ((len = socketChannel.read(byteBuffer)) != -1){
            byteBuffer.flip();
            System.out.println(new String(byteBuffer.array(), 0, len));
            byteBuffer.clear();
        }

        //6、关闭通道
        socketChannel.close();
        fileChannel.close();
    }

    @Test
    public void serverBlocking2() throws IOException {
        //1、获取通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        FileChannel fileChannel = FileChannel.open(Paths.get("通道1-副本.png"), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        //2、绑定端口
        serverSocketChannel.bind(new InetSocketAddress(8888));
        //3、获取客户端连接
        SocketChannel socketChannel = serverSocketChannel.accept();
        //4、分配指定大小的缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        //5、读取客户端传来的文件,保存到本地
        while (socketChannel.read(byteBuffer) != -1){
            byteBuffer.flip();
            fileChannel.write(byteBuffer);
            byteBuffer.clear();
        }

        //6、关闭对客户端的读取
        socketChannel.shutdownInput();

        //7、发送消息给客户端
        byteBuffer.put("服务端接收数据成功".getBytes());
        byteBuffer.flip();
        socketChannel.write(byteBuffer);

        //8、关闭通道
        serverSocketChannel.close();
        fileChannel.close();
        socketChannel.close();
    }
}
