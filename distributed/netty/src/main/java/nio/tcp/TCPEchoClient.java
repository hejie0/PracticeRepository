package nio.tcp;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class TCPEchoClient implements Runnable {

    private Logger log = LoggerFactory.getLogger(getClass());

    //客户端线程名
    private String name;
    private Random rnd = new Random();
    Charset utf8 = Charset.forName("UTF-8");
    //服务器的ip地址+端口port
    private InetSocketAddress address;

    public TCPEchoClient(String name, InetSocketAddress address) {
        this.name = name;
        this.address = address;
    }

    @Override
    public void run() {
        Selector selector = null;
        SocketChannel sc = null;
        try {
            selector = Selector.open(); //创建选择器
            sc = SocketChannel.open(); //创建TCP通道
            sc.configureBlocking(false); //设置为NIO(同步非阻塞IO)

            int interestSet = SelectionKey.OP_READ | SelectionKey.OP_WRITE; //注册事件
            sc.register(selector, interestSet); //向选择器注册通道

            sc.connect(address); //向服务器发起连接，一个通道代表一条TCP连接

            while (!sc.finishConnect()) { } //等待3次握手完成

            log.info("{} finished connection", name);
        } catch (IOException e) {
            log.error("client connect failed");
            log.error("{}", ExceptionUtils.getStackTrace(e));
            Utils.close(sc, selector);
            return;
        }

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

                    if (key.isReadable()) {
                        handleRead(key);
                    }
                    if (key.isWritable()) {
                        handleWrite(key);
                    }
                }
            }
        } catch (IOException e) {

        } finally {
            Utils.close(sc, selector);
        }
    }

    public void handleRead(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        ByteBuffer buf = (ByteBuffer) key.attachment();
        long bytesRead = sc.read(buf);
        while (bytesRead > 0) {
            buf.flip();
            while (buf.hasRemaining()) {
                CharBuffer cb = utf8.decode(buf);
                log.info("{}", cb);
                log.info("{}", cb.array());
            }
            buf.clear();
            bytesRead = sc.read(buf);
        }
        if (bytesRead == -1) {
            sc.close();
        }
    }

    public void handleWrite(SelectionKey key) throws IOException {
        ByteBuffer buf = (ByteBuffer) key.attachment();
        buf.flip();
        SocketChannel sc = (SocketChannel) key.channel();
        while (buf.hasRemaining()) {
            sc.write(buf);
        }
        buf.compact(); //压缩数据
    }

    public static void main(String[] args) throws Exception {
        InetSocketAddress address = new InetSocketAddress(7777);
        Thread a = new Thread(new TCPEchoClient("a", address));
        Thread b = new Thread(new TCPEchoClient("b", address));
        Thread c = new Thread(new TCPEchoClient("c", address));
        Thread d = new Thread(new TCPEchoClient("d", address));

        a.start();
//        b.start();
//        c.start();
//        Thread.sleep(5000);
//
//        //结束客户端
//        a.interrupt();
//        b.interrupt();
//        //启动客户端d
//        d.start();
    }
}
