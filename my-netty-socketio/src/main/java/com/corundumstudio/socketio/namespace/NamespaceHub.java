package com.corundumstudio.socketio.namespace;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import io.netty.util.internal.PlatformDependent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

public class NamespaceHub {

    private final ConcurrentMap<String, SocketIONamespace> namespaces = PlatformDependent.newConcurrentHashMap();
    private final Configuration configuration;

    public NamespaceHub(Configuration configuration) {
        this.configuration = configuration;
    }

    public Namespace create(String name) {
        Namespace namespace = (Namespace) namespaces.get(name);
        if (namespace == null) {
            namespace = new Namespace(name, configuration);
            Namespace oldNamespace = (Namespace) namespaces.putIfAbsent(name, namespace);
            if (oldNamespace != null) {
                namespace = oldNamespace;
            }
        }
        return namespace;
    }

    public Iterator<SocketIOClient> getRoomClients(String room) {
        List<Iterator<SocketIOClient>> allClients = new ArrayList<>();
        for (SocketIONamespace namespace : namespaces.values()) {
            ((Namespace)namespace).getRoomClients(room);
        }
    }

    public Namespace get(String name) {
        return (Namespace) namespaces.get(name);
    }

    public void remove(String name) {
        SocketIONamespace namespace = namespaces.remove(name);
        if (namespace != null) {
            namespace.getBroadcastOperations().disconnect();
        }
    }

    public Collection<SocketIONamespace> getAllNamespace() {
        return namespaces.values();
    }
}
