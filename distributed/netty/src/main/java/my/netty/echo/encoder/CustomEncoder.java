package my.netty.echo.encoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Encoder对应的就是ChannelOutboundHandler，消息对象转换为字节数组
 * Netty本身未提供和解码一样的编码器，是因为场景不同，两者非对等的
 * MessageToByteEncoder消息转为字节数组,调用write方法，会先判断当前编码器是否支持需要发送的消息类型，如果不支持，则透传；
 * MessageToMessageEncoder用于从一种消息编码为另外一种消息（例如POJO到POJO）
 */
public class CustomEncoder extends MessageToByteEncoder<Integer> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Integer msg, ByteBuf out) throws Exception {
        out.writeInt(msg);
    }
}
