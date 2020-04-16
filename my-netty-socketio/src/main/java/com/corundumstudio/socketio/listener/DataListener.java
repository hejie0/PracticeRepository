package com.corundumstudio.socketio.listener;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;

public interface DataListener<T> {

    /**
     * 从客户端接收数据对象时调用
     * Invokes when data object received from client
     *
     * @param client - receiver
     * @param data - received object
     * @param ackSender - ack request
     *
     * @throws Exception
     */
    void onData(SocketIOClient client, T data, AckRequest ackSender) throws Exception;

}
