package com.corundumstudio.socketio;

import com.corundumstudio.socketio.protocol.Packet;

public interface ClientOperations {

    /**
     * 发送自定义数据包。
     * Send custom packet.
     * @param packet
     */
    void send(Packet packet);

    /**
     * 断开客户端
     * Disconnect client
     */
    void disconnect();

    /**
     * 发送事件
     * Send event
     * @param name event name
     * @param data event data
     */
    void sendEvent(String name, Object... data);
}
