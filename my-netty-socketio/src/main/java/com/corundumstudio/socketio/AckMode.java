package com.corundumstudio.socketio;

public enum  AckMode {

    /**
     * 对每个ack请求自动发送ack-response
     * 跳过包处理期间的异常
     * Send ack-response automatically on each ack-request
     * <b>skip</b> exceptions during packet handling
     */
    AUTO,

    /**
     * 对每个ack请求自动发送ack-response
     * 只有在成功包处理之后
     * Send ack-response automatically on each ack-request
     * only after <b>success</b> packet handling
     */
    AUTO_SUCCESS_ONLY,

    /**
     * 关闭自动应答发送。
     * 使用AckRequest。每次发送ackdata来发送ack-response。
     * Turn off auto ack-response sending.
     * Use AckRequest.sendAckData to send ack-response each time.
     */
    MANUAL
}
