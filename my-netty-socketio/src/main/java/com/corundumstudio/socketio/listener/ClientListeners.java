package com.corundumstudio.socketio.listener;

public interface ClientListeners {

    void addMultiTypeEventListener(String eventName, MultiTypeEventListener listener, Class<?>... eventClass);

    <T> void addEventListener(String eventName, Class<T> eventClass, DataListener<T> listener);

    void addEventInterceptor(EventInterceptor eventInterceptor);

    void addDisconnectListener(DisconnectListener listener);

    void addConnectListener(ConnectListener listener);

    void addPingListener(PingListener listener);

    void addListeners(Object listeners);

    void addListeners(Object listeners, Class<?> listenersClass);

    void removeAllListeners(String eventName);

}
