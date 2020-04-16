package com.corundumstudio.socketio;

import com.corundumstudio.socketio.protocol.AckArgs;
import com.corundumstudio.socketio.protocol.JsonSupport;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class JsonSupportWrapper implements JsonSupport {

    private static final Logger log = LoggerFactory.getLogger(JsonSupportWrapper.class);

    private final JsonSupport delegate;

    JsonSupportWrapper(JsonSupport delegate){
        this.delegate = delegate;
    }

    @Override
    public AckArgs readAckArgs(ByteBufInputStream src, AckCallBack<?> callBack) throws IOException {
        return null;
    }

    @Override
    public <T> T readValue(String namespaceName, ByteBufInputStream src, Class<T> valueType) throws IOException {
        return null;
    }

    @Override
    public void writeValue(ByteBufOutputStream out, Object value) throws IOException {

    }

    @Override
    public void addEventMapping(String namespaceName, String eventName, Class<?>... eventClass) {

    }

    @Override
    public void removeEventMapping(String namespaceName, String eventName) {

    }

    @Override
    public List<byte[]  > getArrays() {
        return null;
    }
}
