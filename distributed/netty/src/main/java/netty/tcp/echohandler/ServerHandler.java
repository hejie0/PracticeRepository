package netty.tcp.echohandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.tcp.pojo.Message;

import java.util.concurrent.atomic.AtomicInteger;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    private AtomicInteger counter = new AtomicInteger(0);

    /*@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //半包读写问题
        ByteBuf buf = (ByteBuf)msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);

        String body = new String(bytes, "UTF-8")
                .substring(0, bytes.length - System.getProperty("line.separator").length());
        System.out.println("服务端收到消息内容为: " + body + ", 收到消息次数: " + counter.incrementAndGet());
    }*/

    /*@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String body = (String)msg;
        System.out.println("服务端收到消息内容为: " + body + ", 收到消息次数: " + counter.incrementAndGet());
    }*/

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message myProtocolBean = (Message)msg;  //直接转化成协议消息实体
        System.out.println("服务端收到消息内容为: " + myProtocolBean.getContent() + ", 收到消息次数: " + counter.incrementAndGet());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
