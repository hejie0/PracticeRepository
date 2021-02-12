package netty.demo.filesync.xproto;

public class MessageClearSession extends netty.demo.filesync.xproto.Message {

    public MessageClearSession(MessageType type) {
        super(netty.demo.filesync.xproto.MessageType.CLEAR_SESSION);
    }
}
