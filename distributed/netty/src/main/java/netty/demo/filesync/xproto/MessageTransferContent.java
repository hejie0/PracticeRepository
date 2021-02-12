package netty.demo.filesync.xproto;

import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MessageTransferContent extends MessageTransferSession {

    private Logger log = LoggerFactory.getLogger(getClass());
    private long sequence;
    private ByteBuf data;

    public MessageTransferContent(MessageType type) {
        super(netty.demo.filesync.xproto.MessageType.TRANSFER_CONTENT);
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public ByteBuf getData() {
        return data;
    }

    public void setData(ByteBuf data) {
        this.data = data;
    }

    // release file content buffer to buffer pool
    public void release() {
        data.release();
    }

    @Override
    public void encode(ByteBuf buf) {
        super.encode(buf);
        buf.writeLong(sequence);
        buf.writeBytes(data);
    }

    @Override
    public void decode(ByteBuf buf) {
        super.decode(buf);
        sequence = buf.readLong();
        // because MessageTranferContent gets the buffer's ownership, increase reference count.
        buf.retain();
        data = buf;
    }
}
