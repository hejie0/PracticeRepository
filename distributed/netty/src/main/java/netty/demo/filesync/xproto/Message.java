package netty.demo.filesync.xproto;

import io.netty.buffer.ByteBuf;

public class Message {

    public static int MAGIC = 0xFEEFA0C0;
    protected int magic;
    protected MessageType type;

    public Message(MessageType type) {
        this.type = type;
    }

    public void encode(ByteBuf buf) {
        buf.writeInt(MAGIC);
        buf.writeByte(type.ordinal());
    }

    public void decode(ByteBuf buf) {
        magic = buf.readInt();
        type = MessageType.values()[buf.readByte()];
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                '}';
    }
}
