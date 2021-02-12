package netty.demo.filesync.sender;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import netty.demo.filesync.xproto.XProtoMessageEncoder;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MsgSender {

    private Logger log = LoggerFactory.getLogger(getClass());

    private String host = "127.0.0.1";
    private int port = 55555;

    public void start(){
        EventLoopGroup guestGroup = new NioEventLoopGroup();
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(guestGroup)
                    .remoteAddress(host, port)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new XProtoMessageEncoder());

            ChannelFuture channelFuture =bootstrap.connect().sync();
            channelFuture.channel().closeFuture().sync();

        }catch (InterruptedException e){
            log.error("error: {}", ExceptionUtils.getStackTrace(e));
        }finally {
            guestGroup.shutdownGracefully();
        }

    }
}
