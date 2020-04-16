package com.corundumstudio.socketio.listener;

import com.corundumstudio.socketio.SocketIOClient;

public interface DisconnectListener {

    void onDisconnection(SocketIOClient client);

}
