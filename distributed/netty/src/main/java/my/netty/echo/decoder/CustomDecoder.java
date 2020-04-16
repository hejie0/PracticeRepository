package my.netty.echo.decoder;

import my.netty.echo.pojo.Student;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * ByteToMessageDecoder用于将字节转为消息，需要检查缓冲区是否有足够的字节
 * ReplayingDecoder继承ByteToMessageDecoder，不需要检查缓冲区是否有足够的字节，但是ReplayingDecoder速度略满于ByteToMessageDecoder，不是所有的ByteBuf都支持
 * 选择：项目复杂性高则使用ReplayingDecoder，否则使用 ByteToMessageDecoder
 * MessageToMessageDecoder用于从一种消息解码为另外一种消息（例如POJO到POJO）
 *
 * DelimiterBasedFrameDecoder： 指定消息分隔符的解码器
 * LineBasedFrameDecoder: 以换行符为结束标志的解码器
 * FixedLengthFrameDecoder：固定长度解码器
 * LengthFieldBasedFrameDecoder：message = header+body, 基于长度解码的通用解码器
 * StringDecoder：文本解码器，将接收到的对象转化为字符串，一般会与上面的进行配合，然后在后面添加业务handle
 */
public class CustomDecoder extends MessageToMessageDecoder<Student> {
    @Override
    protected void decode(ChannelHandlerContext ctx, Student msg, List<Object> out) throws Exception {
        out.add(msg);
    }
}
