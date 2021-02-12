package netty.demo.filesync.xproto;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class XProtoMessageDecoder extends MessageToMessageDecoder<Message> {

    @Override
    protected void decode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
        out.add(msg);
    }
}
