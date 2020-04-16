package com.corundumstudio.socketio.transport;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.protocol.Packet;

public class NamespaceClient implements SocketIOClient {
    @Override
    public void send(Packet packet) {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public void sendEvent(String name, Object... data) {

    }

    @Override
    public void set(String key, Object val) {

    }

    @Override
    public <T> T get(String key) {
        return null;
    }

    @Override
    public boolean has(String key) {
        return false;
    }

    @Override
    public void del(String key) {

    }
}
