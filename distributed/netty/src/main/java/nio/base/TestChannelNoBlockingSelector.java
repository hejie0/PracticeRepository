package nio.base;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;

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
public class TestChannelNoBlockingSelector {

    @Test
    public void client() throws IOException {
        //1、获取通道
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8888));
        //2、切换为非阻塞模式
        socketChannel.configureBlocking(false);
        //3、分配指定大小的缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        //4、发送数据给服务端
        Scanner scanner = new Scanner(System.in);
        while (true){
            String line = scanner.next();
            if ("over".equalsIgnoreCase(line)) break;

            byteBuffer.put((line + "--" +new Date().toString()).getBytes());
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
            byteBuffer.clear();
        }
        //5、关闭通道
        socketChannel.close();
    }

    @Test
    public void server() throws IOException {
        //1、获取通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //2、切换为非阻塞模式
        serverSocketChannel.configureBlocking(false);
        //3、绑定端口
        serverSocketChannel.bind(new InetSocketAddress(8888));
        //4、获取选择器
        Selector selector = Selector.open();
        //5、将通道注册到选择器上，并且指定“监听接收事件”
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        //6、轮询试的获取选择器上已经“准备就绪”的事件
        while(selector.select() > 0){
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            //7、获取当前选择器中所有注册的“选择键(已经就绪的监听事件)”
            while(it.hasNext()){
                //8、获取准备“就绪”的事件
                SelectionKey selectionKey = it.next();
                //9、判断具体是什么事件准备就绪
                if(selectionKey.isAcceptable()){
                    //10、若“接收就绪”，获取客户端连接
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    //11、切换非阻塞模式
                    socketChannel.configureBlocking(false);
                    //12、将该通道注册到选择器上
                    socketChannel.register(selector, SelectionKey.OP_READ);
                }else if (selectionKey.isConnectable()){

                }else if (selectionKey.isReadable()){
                    //13、获取当前选择器上“读就绪”状态的通道
                    SocketChannel socketChannel = (SocketChannel)selectionKey.channel();
                    //14、读取数据
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    int len = 0;
                    while ((len = socketChannel.read(byteBuffer)) > 0){
                        byteBuffer.flip();
                        System.out.println(new String(byteBuffer.array(), 0, len));
                        byteBuffer.clear();
                    }
                }else if (selectionKey.isWritable()){

                }else if (selectionKey.isValid()){

                }
                it.remove(); //使用完后，一定要取消
            }
        }

    }

}
