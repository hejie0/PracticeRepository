package netty.demo.filesync.xproto;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import netty.demo.filesync.utils.Utils;

import java.util.List;

public class XProtoMessageEncoder extends MessageToMessageEncoder<Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
        ByteBuf buf = ctx.channel().alloc().heapBuffer(Utils.MAX_BUFFER_SIZE);
        msg.encode(buf);
    }
}
