package my.netty.echo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import my.netty.echo.echohandler.EchoClientHandler;

public class EchoClient {

    private String host;

    private int port;

    public EchoClient(String host, int port){
        this.host = host;
        this.port = port;
    }

    public void start(){
        EventLoopGroup guestGroup = new NioEventLoopGroup();
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(guestGroup)
                    .remoteAddress(host, port)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline channelPipeline = ch.pipeline();
                            channelPipeline.addLast(new EchoClientHandler());
                        }
                    });

            //连接到服务端，connect是异步连接，在调用同步等待sync，等待连接
            ChannelFuture channelFuture =bootstrap.connect().sync();
            //阻塞，直到客户端通道关闭
            channelFuture.channel().closeFuture().sync();

        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            //优雅退出，释放NIO线程组
            guestGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args){
        new EchoClient("127.0.0.1", 8080).start();
    }

}
