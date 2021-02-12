package nio.udp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class UDPServer {

    private static Logger log = LoggerFactory.getLogger(UDPServer.class);

    public static void main(String[] args) throws Exception {
        DatagramChannel channel=DatagramChannel.open();
        channel.socket().bind(new InetSocketAddress(9999));

        ByteBuffer buf=ByteBuffer.allocate(1024);
        /*阻塞，等待发来的数据*/
        channel.receive(buf);
        /*设置缓冲区可读*/
        buf.flip();

        byte[] dst = new byte[1024];
        /*循环读出所有字符*/
        while(buf.hasRemaining()) {
            buf.get(dst);
            log.info("{}", new String(dst));;
        }
        channel.close();
    }
}
