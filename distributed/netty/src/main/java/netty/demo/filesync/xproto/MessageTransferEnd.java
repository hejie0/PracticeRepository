package netty.demo.filesync.xproto;

public class MessageTransferEnd extends MessageTransferSession {

    public MessageTransferEnd(MessageType type) {
        super(netty.demo.filesync.xproto.MessageType.TRANSFER_END);
    }
}
