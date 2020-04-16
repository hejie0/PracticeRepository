package my.netty.echo.echohandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import my.netty.echo.pojo.Message;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    /*@Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBuf mes = null;
        byte[] req = ("hello 你好 world 世界" + System.getProperty("line.separator")).getBytes();
        //连续发送100次
        for(int i = 0; i < 100; i++){
            mes = Unpooled.buffer(req.length);
            mes.writeBytes(req);
            ctx.writeAndFlush(mes);
        }
    }*/

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Message myProtocolBean = null;
        for(int i = 0; i < 10; i++){
            myProtocolBean = new Message("Hello,Netty".length(), "Hello,Netty");
            ctx.writeAndFlush(myProtocolBean);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
