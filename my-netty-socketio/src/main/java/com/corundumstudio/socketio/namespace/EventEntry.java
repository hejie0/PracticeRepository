package com.corundumstudio.socketio.namespace;

import com.corundumstudio.socketio.listener.DataListener;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EventEntry<T> {

    private final Queue<DataListener<T>> listeners = new ConcurrentLinkedQueue<>();

    public EventEntry() {

    }

    public void addListener(DataListener<T> listener) {
        listeners.add(listener);
    }

    public Queue<DataListener<T>> getListeners(){
        return listeners;
    }

}
