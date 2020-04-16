package com.corundumstudio.socketio.store;

import com.corundumstudio.socketio.Disconnectable;
import com.corundumstudio.socketio.handler.AuthorizeHandler;
import com.corundumstudio.socketio.namespace.NamespaceHub;
import com.corundumstudio.socketio.protocol.JsonSupport;
import com.corundumstudio.socketio.store.pubsub.PubSubStore;

import java.util.Map;
import java.util.UUID;

public interface StoreFactory extends Disconnectable {

    PubSubStore pubSubStore();

    <K, V> Map<K, V> createMap(String name);

    Store createStore(UUID sessionId);

    void init(NamespaceHub namespaceHub, AuthorizeHandler authorizeHandler, JsonSupport jsonSupport);

    void shutdown();
}
