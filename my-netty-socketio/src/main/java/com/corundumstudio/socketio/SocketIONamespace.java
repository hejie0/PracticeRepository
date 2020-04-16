package com.corundumstudio.socketio;

import com.corundumstudio.socketio.listener.ClientListeners;

import java.util.Collection;
import java.util.UUID;

/**
 * 完全线程安全的。
 * Fully thread-safe.
 */
public interface SocketIONamespace extends ClientListeners {

    String getName();

    BroadcastOperations getBroadcastOperations();

    BroadcastOperations getRoomOperations(String room);

    /**
     * 获取所有客户端连接到名称空间
     * Get all clients connected to namespace
     * @return collection of clients
     */
    Collection<SocketIOClient> getAllClients();

    /**
     * 通过连接到名称空间的uuid获取客户端
     * Get client by uuid connected to namespace
     * @param uuid - id of client
     * @return client
     */
    SocketIOClient getClient(UUID uuid);
}
