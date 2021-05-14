package netty.demo.filesync.xproto;

public class MessageClearSession extends Message {

    public MessageClearSession(MessageType type) {
        super(MessageType.CLEAR_SESSION);
    }
}
