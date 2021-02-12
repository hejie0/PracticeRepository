package nio.udp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class UDPClient {

    private static Logger log = LoggerFactory.getLogger(UDPClient.class);

    public static void main(String[] args) throws Exception {
        DatagramChannel channel = DatagramChannel.open();

        String newData = "hello nio udp server";
        ByteBuffer buf = ByteBuffer.allocate(1024);
        buf.put(newData.getBytes());
        buf.flip();
        /*发送UDP数据包*/
        int bytesSent = channel.send(buf, new InetSocketAddress("127.0.0.1", 9999));
        channel.close();
    }
}
