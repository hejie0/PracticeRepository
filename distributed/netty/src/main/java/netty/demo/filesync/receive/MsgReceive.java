package netty.demo.filesync.receive;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import netty.demo.filesync.xproto.XProtoMessageDecoder;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class MsgReceive {

    private Logger log = LoggerFactory.getLogger(getClass());

    private String host = "127.0.0.1";
    private int port = 55555;

    public void start() {
        EventLoopGroup boosGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boosGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .childHandler(new XProtoMessageDecoder());

            InetSocketAddress address = new InetSocketAddress(host, port);
            ChannelFuture channelFuture = serverBootstrap.bind(address).sync();
            channelFuture.channel().closeFuture().sync();

            System.out.println("message send server start ");
        } catch (Exception e) {
            log.error("error: {}", ExceptionUtils.getStackTrace(e));
        } finally {
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

}
