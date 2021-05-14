package netty.demo.filesync.xproto;

public class MessageTransferCancel extends MessageTransferSession {

    public MessageTransferCancel(MessageType type) {
        super(MessageType.TRANSFER_CANCEL);
    }
}
