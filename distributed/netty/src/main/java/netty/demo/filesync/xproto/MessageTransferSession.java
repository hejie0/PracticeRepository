package netty.demo.filesync.xproto;

import io.netty.buffer.ByteBuf;

public class MessageTransferSession extends netty.demo.filesync.xproto.Message {

    protected long sessionId;

    public MessageTransferSession(MessageType type) {
        super(type);
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public void encode(ByteBuf buf) {
        super.encode(buf);
        buf.writeLong(sessionId);
    }

    @Override
    public void decode(ByteBuf buf) {
        super.decode(buf);
        sessionId = buf.readInt();
    }

    @Override
    public String toString() {
        return "MessageTransferSession{" +
                "sessionId=" + sessionId +
                ", type=" + type +
                '}';
    }
}
