package netty.demo.filesync.xproto;

public class MessageTransferEnd extends MessageTransferSession {

    public MessageTransferEnd(MessageType type) {
        super(MessageType.TRANSFER_END);
    }
}
