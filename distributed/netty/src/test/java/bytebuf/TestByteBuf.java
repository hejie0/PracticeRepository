package bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

/**
 * ByteBuf分为4种：
 * 池化堆缓冲区: PooledHeapByteBuf
 * 池化堆外缓冲区: PooledDirectByteBuf
 * 非池化堆缓冲区: UnpooledHeapByteBuf
 * 非池化堆外缓冲区: UnpooledDirectByteBuf
 *
 * 创建ByteBuf的类
 * Unpooled
 * ByteBufAllocator
 *      |PooledByteBufAllocator
 *      |UnpooledByteBufAllocator
 */
public class TestByteBuf {

    @Test
    public void testByteBuf(){
        //组合缓冲区
        CompositeByteBuf compositeByteBuf = Unpooled.compositeBuffer();
        //创建一个堆缓冲区
        ByteBuf heapBuffer = Unpooled.buffer(16);
        //创建堆外缓冲区
        ByteBuf directBuffer = Unpooled.directBuffer(16);

        compositeByteBuf.addComponents(heapBuffer, directBuffer);

    }

}
