package millionconnect;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import millionconnect.config.Config;

public class NettyClient {

    private static final String SERVER = "192.168.50.155";

    public static void main(String[] args){
        new NettyClient().run(Config.BEGIN_PORT, Config.END_PORT);
    }

    private void run(int beginPort, int endPort){
        System.out.println("客户端启动中");

        EventLoopGroup group = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_REUSEADDR, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {

                    }
                });

        for (; beginPort < endPort; beginPort++){
            int port = beginPort;
            bootstrap.connect(SERVER, port).addListener((ChannelFutureListener) future -> {
                if(!future.isSuccess()){
                    System.out.println("创建连接失败port: " + port);
                    //future.cause().printStackTrace(); 这个方法如果一直打印会撑爆内存
                }
            });
            if (beginPort == endPort - 1){
                beginPort = Config.BEGIN_PORT;
            }
        }
    }

}
