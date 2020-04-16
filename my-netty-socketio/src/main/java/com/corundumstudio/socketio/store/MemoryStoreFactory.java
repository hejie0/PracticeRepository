package com.corundumstudio.socketio.store;

import com.corundumstudio.socketio.handler.ClientHead;
import com.corundumstudio.socketio.store.pubsub.BaseStoreFactory;
import com.corundumstudio.socketio.store.pubsub.PubSubStore;

import java.util.Map;
import java.util.UUID;

public class MemoryStoreFactory extends BaseStoreFactory {


    @Override
    public PubSubStore pubSubStore() {
        return null;
    }

    @Override
    public <K, V> Map<K, V> createMap(String name) {
        return null;
    }

    @Override
    public Store createStore(UUID sessionId) {
        return null;
    }

    @Override
    public void shutdown() {

    }

    @Override
    public void onDisconnect(ClientHead client) {

    }
}
