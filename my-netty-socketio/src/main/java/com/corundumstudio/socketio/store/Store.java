package com.corundumstudio.socketio.store;

public interface Store {

    void set(String key, Object val);

    <T> T get(String key);

    boolean has(String key);

    void del(String key);

}
