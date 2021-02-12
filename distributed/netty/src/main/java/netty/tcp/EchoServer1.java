package netty.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import netty.tcp.decoder.MyLengthFieldBasedFrameDecoder;
import netty.tcp.echohandler.ServerHandler;

public class EchoServer1 {

    private int port;

    public EchoServer1(int port){
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
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline channelPipeline = ch.pipeline();
                            //1024表示一行最大的长度，如果超过这个长度依然没有检测自定义分隔符，将会抛出TooLongFrameException
                            //channelPipeline.addLast(new DelimiterBasedFrameDecoder(1024, Unpooled.copiedBuffer(System.getProperty("line.separator"), CharsetUtil.UTF_8)));
                            //channelPipeline.addLast(new LineBasedFrameDecoder(1024)); //解决半包读写问题
                            //channelPipeline.addLast(new StringDecoder());
                            channelPipeline.addLast(new MyLengthFieldBasedFrameDecoder //4表示int占4个字节 long占8个字节
                                    (1024,0,4,0,0,true));
                            channelPipeline.addLast(new ServerHandler());
                        }
                    });
            System.out.println("Echo server start ");

            //绑定端口，同步等待
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
        new EchoServer1(port).run();
    }
}
