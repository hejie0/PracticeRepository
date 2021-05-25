package netty.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import netty.tcp.handler.InboundHandler1;
import netty.tcp.handler.InboundHandler2;
import netty.tcp.handler.OutboundHandler1;
import netty.tcp.handler.OutboundHandler2;

/**
 * serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
 * 服务器端TCP内核模块维护有2个队列synQueue，acceptQueue，客户端向服务端connect的时候，
 * 发送带有SYN标志的包（第一次握手），服务端收到客户端发来的SYN时，向客户端发送SYN ACK 确认(第二次握手)，
 * 此时TCP内核模块把客户端连接加入到A队列中，然后服务器收到客户端发来的ACK时（第三次握手）TCP把客户端连接从A队列移到B队列，
 * 连接完成，应用程序的accept会返回，也就是说accept从B队列中取出完成三次握手的连接，A队列和B队列的长度之和是backlog,当A，
 * B队列的长之和大于backlog时，新连接将会被TCP内核拒绝，所以，如果backlog过小，可能会出现accept速度跟不上，A.B 队列满了，
 * 导致新客户端无法连接，要注意的是，backlog对程序支持的连接数并无影响，backlog影响的只是还没有被accept 取出的连接
 *
 *serverBootstrap.option(ChannelOption.TCP_NODELAY, Boolean.valueOf(true));
 * 在TCP/IP协议中，无论发送多少数据，总是要在数据前面加上协议头，同时，对方接收到数据，也需要发送ACK表示确认。
 * 为了尽可能的利用网络带宽，TCP总是希望尽可能的发送足够大的数据。这里就涉及到一个名为Nagle的算法，
 * 该算法的目的就是为了尽可能发送大块数据，避免网络中充斥着许多小数据块。
 * TCP_NODELAY就是用于启用或关于Nagle算法。
 * 如果要求高实时性，有数据发送时就马上发送，就将该选项设置为true关闭Nagle算法；
 * 如果要减少发送次数减少网络交互，就设置为false等累积一定大小后再发送。默认为false。
 */
public class EchoServer {

    private int port;

    public EchoServer(int port){
        this.port = port;
    }

    /**
     * 启动流程
     */
    public void run() {
        //配置服务端线程组
        EventLoopGroup boosGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try{
            //启动类
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boosGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new ChannelInitializer<ServerSocketChannel>() {
                        @Override
                        protected void initChannel(ServerSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new InboundHandler1());
                            ch.pipeline().addLast(new OutboundHandler1());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.SO_REUSEADDR, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline channelPipeline = ch.pipeline();
                            //生命周期方法执行顺序测试
//                            channelPipeline.addLast(new EchoServerHandler());
//                            channelPipeline.addLast(new EchoServerHandler1());

                            //InboundHandler顺序执行，OutboundHandler逆序执行
                            //所有OutboundHandler如果放在InboundHandler下面，则OutboundHandler不会执行，
                            //但是在InboundHandler中使用Channel、ChannelPipeline的writeXXX方法，则OutboundHandler可以执行，不能使用ctx.writeXXX()
                            channelPipeline.addLast(new InboundHandler1());
                            channelPipeline.addLast(new InboundHandler2());
                            channelPipeline.addLast(new OutboundHandler1());
                            channelPipeline.addLast(new OutboundHandler2());
                        }
                    });
            System.out.println("Echo server start ");

            //绑定端口，同步等待成功
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            //等待服务端监听端口关闭
            channelFuture.channel().closeFuture().sync();
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            //优雅退出，释放线程池
            workGroup.shutdownGracefully();
            boosGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.valueOf(args[0]) : 8081;
        new EchoServer(port).run();
    }
}
