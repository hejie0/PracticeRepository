package netty.demo.filesync.xproto;

public enum MessageType {
    TRANSFER_START,
    TRANSFER_END,
    TRANSFER_CANCEL,
    TRANSFER_CONTENT,
    DIR_CREATE,
    CLEAR_SESSION
}
