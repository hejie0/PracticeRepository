package com.corundumstudio.socketio;

import java.util.Iterator;
import java.util.List;

public class MultiTypeArgs implements Iterator<Object> {

    private final List<Object> args;

    public MultiTypeArgs(List<Object> args) {
        this.args = args;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public Object next() {
        return null;
    }
}
