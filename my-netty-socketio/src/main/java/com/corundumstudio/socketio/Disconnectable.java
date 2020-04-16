package com.corundumstudio.socketio;

import com.corundumstudio.socketio.handler.ClientHead;

public interface Disconnectable {

    void onDisconnect(ClientHead client);

}
