package netty.tcp.echohandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf data = (ByteBuf)msg; //服务端收到数据
        System.out.println("EchoServerHandler channelRead：" + data.toString(CharsetUtil.UTF_8));

        //第一种
        //Channel channel = ctx.channel();
        //channel.writeAndFlush(data);

        //第二种
        //ChannelPipeline channelPipeline = ctx.pipeline();
        //channelPipeline.writeAndFlush(data);

        //第三种
        //ctx.writeAndFlush(data);

        //Channel、ChannelPipeline、ChannelHandlerContext 都可以调用writeAndFlush方法，前两者都会在整个管道流里传播，
        //而ChannelHandlerContext就只会在后续的Handler里面传播

        //data.clear(); //数据需要传递到下一个channelhandler时，不需要清空
        ctx.fireChannelRead(msg); //ctx.fireXXX 调用下一个handler的channelRead方法
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("EchoServerHandler channelReadComplete");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("EchoServerHandler exceptionCaught");
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("EchoServerHandler channelRegistered");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("------------------EchoServerHandler channelActive------------------");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("EchoServerHandler channelInactive");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("EchoServerHandler channelUnregistered");
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("EchoServerHandler handlerAdded");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("------------------EchoServerHandler handlerRemoved------------------");
    }
}
