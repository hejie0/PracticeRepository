package com.corundumstudio.socketio.listener;

import com.corundumstudio.socketio.SocketIOClient;

public interface ConnectListener {

    void onConnect(SocketIOClient client);

}
