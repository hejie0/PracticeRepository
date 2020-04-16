package com.corundumstudio.socketio.listener;

import com.corundumstudio.socketio.SocketIOClient;

public interface PingListener {

    void onPing(SocketIOClient client);

}
