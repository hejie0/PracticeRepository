package com.corundumstudio.socketio.protocol;

import com.corundumstudio.socketio.AckCallBack;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;
import java.util.List;

/**
 * JSON infrastructure interface.
 * Allows to implement custom realizations to JSON support operations.
 */
public interface JsonSupport {

    AckArgs readAckArgs(ByteBufInputStream src, AckCallBack<?> callBack) throws IOException;

    <T> T readValue(String namespaceName, ByteBufInputStream src, Class<T> valueType) throws IOException;

    void writeValue(ByteBufOutputStream out, Object value) throws IOException;

    void addEventMapping(String namespaceName, String eventName, Class<?>... eventClass);

    void removeEventMapping(String namespaceName, String eventName);

    List<byte[]> getArrays();

}
