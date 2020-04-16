package com.corundumstudio.socketio;

import com.corundumstudio.socketio.transport.PollingTransport;
import com.corundumstudio.socketio.transport.WebSocketTransport;

public enum Transport {

    WEBSOCKET(WebSocketTransport.NAME),
    POLLING(PollingTransport.NAME);

    private final String value;

    Transport(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Transport byName(String value){
        for (Transport t : Transport.values()){
            if (t.getValue().equals(value)){
                return t;
            }
        }
        throw new IllegalArgumentException("Can't find " + value + " transport");
    }
}
