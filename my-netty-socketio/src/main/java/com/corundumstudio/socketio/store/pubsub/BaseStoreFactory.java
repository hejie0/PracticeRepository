package com.corundumstudio.socketio.store.pubsub;

import com.corundumstudio.socketio.handler.AuthorizeHandler;
import com.corundumstudio.socketio.namespace.NamespaceHub;
import com.corundumstudio.socketio.protocol.JsonSupport;
import com.corundumstudio.socketio.store.StoreFactory;

public abstract class BaseStoreFactory implements StoreFactory {

    @Override
    public void init(NamespaceHub namespaceHub, AuthorizeHandler authorizeHandler, JsonSupport jsonSupport) {

    }
}
