package netty.demo.millionconnect.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 在handler加上注解 @ChannelHandler.Sharable并不是单例，并不是共享这一个handler对象
 * 具体参考：https://blog.csdn.net/dyingstraw/article/details/97303124
 *
 * ChannelInitializer全程只有一个，可以在这里做一些统计的事情
 * 为什么要把handler作为单例使用？
 * 1.方便统计一些信息，如连接数
 * 2.方便再所有channel值间共享以下而信息
 * 3.但是要注意线程同步的问题
 */
@ChannelHandler.Sharable
public class TcpServerCountHandler extends ChannelInboundHandlerAdapter {

    private AtomicInteger count = new AtomicInteger(0);

    public TcpServerCountHandler(){
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            System.out.println("当前连接数为: " + count.get());
            //System.out.println(getClass().toGenericString() + ":" + this); //打印多个connection是不是同一个handler
        }, 0,3, TimeUnit.SECONDS);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("服务端连接断开: " + count.decrementAndGet());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("服务端连接成功: " + count.incrementAndGet());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("TcpServerCountHandler exceptionCaught");
        cause.printStackTrace();
    }
}
