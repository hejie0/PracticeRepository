package my.netty.echo.encoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import my.netty.echo.pojo.Message;

import java.nio.charset.Charset;

public class MyLengthFieldBasedFrameEncoder extends MessageToByteEncoder<Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        if(msg == null){
            throw new Exception("msg is null");
        }
        out.writeInt(msg.getLength()); //int 4个字节
        out.writeBytes(msg.getContent().getBytes(Charset.forName("UTF-8"))); //Hello,Netty 共11个字节
        //out一共15个字节
    }
}
