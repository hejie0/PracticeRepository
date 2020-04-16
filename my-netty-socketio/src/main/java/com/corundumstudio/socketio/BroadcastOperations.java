package com.corundumstudio.socketio;

import com.corundumstudio.socketio.protocol.Packet;
import com.corundumstudio.socketio.store.StoreFactory;

import java.util.Iterator;

/**
 * 完全线程安全的。
 * Fully thread-safe.
 */
public class BroadcastOperations implements ClientOperations {

    private final Iterator<SocketIOClient> clients;
    private final StoreFactory storeFactory;

    public BroadcastOperations(Iterator<SocketIOClient> clients, StoreFactory storeFactory) {
        this.clients = clients;
        this.storeFactory = storeFactory;
    }

    @Override
    public void send(Packet packet) {

    }

    @Override
    public void disconnect() {
        while (clients.hasNext()){
            SocketIOClient client = clients.next();
            client.disconnect();
        }
    }

    @Override
    public void sendEvent(String name, Object... data) {

    }
}
