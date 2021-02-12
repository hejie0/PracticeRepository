package nio.tcp;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class TCPEchoServer implements Runnable {

    private Logger log = LoggerFactory.getLogger(getClass());

    //服务器地址
    private InetSocketAddress address;
    private Charset utf8 = Charset.forName("UTF-8");

    public TCPEchoServer(int port) {
        this.address = new InetSocketAddress(port);
    }

    @Override
    public void run() {
        Selector selector = null;
        ServerSocketChannel ssc = null;

        try {
            selector = Selector.open(); //创建选择器
            ssc = ServerSocketChannel.open(); //创建服务器通道
            ssc.configureBlocking(false); //设置为NIO(同步非阻塞IO)
            ssc.bind(address, 100);  //绑定服务器地址，设置最大连接缓冲数为100
            ssc.register(selector, SelectionKey.OP_ACCEPT); //注册事件
        } catch (IOException e) {
            log.error("server start failed");
            log.error("{}", ExceptionUtils.getStackTrace(e));
            Utils.close(ssc, selector);
            return;
        }

        log.info("server start with address: {}", address);

        try {
            while (!Thread.currentThread().isInterrupted()) {
                int n = selector.select();  //阻塞等待
                if (n == 0) {
                    continue;
                }

                Set<SelectionKey> keySet = selector.selectedKeys(); //Set中的每个key代表一个通道
                Iterator<SelectionKey> it = keySet.iterator();

                //遍历每个已经就绪的通道，处理这个通道已经就绪的事件
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove(); //防止下次select方法返回已处理过的通道

                    //若发生异常，说明客户端连接出现问题，但服务端要保持正常
                    try {
                        if (!key.isValid()) {
                            continue;
                        }

                        if (key.isConnectable()) {
                            log.info("a connection was established with a remote server");
                        }

                        if (key.isAcceptable()) {
                           handleAccept(key);
                        }

                        if (key.isReadable()) {
                            log.info("a channel is ready for reading");
                            handleRead(key); //打印客户端发送的内容
                        }

                        if (key.isWritable()) {
                            log.info("a channel is ready for writing");
                            handleWrite(key);
                        }
                    } catch (Exception e) {
                        log.error("{}", ExceptionUtils.getStackTrace(e));
                    }
                }
            }
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
        } finally {
            Utils.close(ssc, selector);
        }
    }

    public void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel ssChannel = (ServerSocketChannel) key.channel();
        //accept方法会返回一个socket通道，每个通道在内核中都对应一个socket缓冲区
        SocketChannel sc = ssChannel.accept();
        sc.configureBlocking(false);
        //向选择器注册这个socket通道和事件，同时提供这个新通道相关的缓冲区
        sc.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(1024));
    }

    public void handleRead(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        ByteBuffer buf = (ByteBuffer) key.attachment();
        int len = 0;
        while ((len = sc.read(buf)) > 0) {
            buf.flip();
            System.out.println(new String(buf.array(), 0, len));
            buf.clear();
        }
        key.interestOps(key.interestOps() | key.OP_WRITE); //设置通道写事件
    }

    public void handleWrite(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        ByteBuffer buf = (ByteBuffer) key.attachment();
        while (buf.hasRemaining()) {
            buf.flip();
            sc.write(buf);
        }
        buf.compact(); //压缩数据
        key.interestOps(key.interestOps() & (~SelectionKey.OP_WRITE)); //取消通道的写事件
    }

    public static void main(String[] args) {
        new Thread(new TCPEchoServer(7777)).start();
    }
}