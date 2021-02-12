package netty.demo.millionconnect;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import netty.demo.millionconnect.config.Config;
import netty.demo.millionconnect.handler.TcpServerCountHandler;

public class NettyServer {

    public static void main(String[] args){
        new NettyServer().run(Config.BEGIN_PORT, Config.END_PORT);
    }

    private void run(int beginPort, int endPort){
        System.out.println("服务器启动中");

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                /*.childHandler(new ChannelInitializer<SocketChannel>() { //ChannelInitializer全程只有一个，可以在这里做一些统计的事情
                    //因为handler加上 @ChannelHandler.Sharable 注解并不是单例，
                    //所以在这里创建handler成员变量，才是多个connection使用同一个handler
                    private TcpServerCountHandler tcpServerCountHandler = new TcpServerCountHandler();
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception { //ChannelInitializer是单例，但是init方法每一次创建连接都会被调用
                        ChannelPipeline channelPipeline = ch.pipeline();
                        channelPipeline.addLast(tcpServerCountHandler);
                    }
                });*/
                .childHandler(new TcpServerCountHandler()); //这个也是单例

        for (; beginPort < endPort; beginPort++){
            int port = beginPort;
            serverBootstrap.bind(port).addListener((ChannelFutureListener) future -> {
                System.out.println("服务端成功绑定端口port: " + port);
            });
        }
    }
}
