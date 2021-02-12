package netty.demo.filesync.xproto;

public class MessageTransferCancel extends MessageTransferSession {

    public MessageTransferCancel(MessageType type) {
        super(netty.demo.filesync.xproto.MessageType.TRANSFER_CANCEL);
    }
}
