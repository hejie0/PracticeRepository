package com.corundumstudio.socketio;

import com.corundumstudio.socketio.listener.*;
import com.corundumstudio.socketio.namespace.Namespace;
import com.corundumstudio.socketio.namespace.NamespaceHub;
import io.netty.channel.EventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 完全线程安全的。
 * Fully thread-safe.
 */
public class SocketIOServer implements ClientListeners {

    private static final Logger log = LoggerFactory.getLogger(SocketIOServer.class);

    private final Configuration configCopy;
    private final Configuration configuration;

    private final NamespaceHub namespaceHub;
    private final SocketIONamespace mainNamespace;

    private SocketIOChannelInitializer pipelineFactory = new SocketIOChannelInitializer();

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public SocketIOServer(Configuration configuration) {
        this.configuration = configuration;
        this.configCopy = new Configuration(configuration);
        this.namespaceHub = new NamespaceHub(configCopy);
        this.mainNamespace = addNamespace(Namespace.DEFAULT_NAME);
    }

    private SocketIONamespace addNamespace(String name) {
        return namespaceHub.create(name);
    }

    @Override
    public void addMultiTypeEventListener(String eventName, MultiTypeEventListener listener, Class<?>... eventClass) {

    }

    @Override
    public <T> void addEventListener(String eventName, Class<T> eventClass, DataListener<T> listener) {

    }

    @Override
    public void addEventInterceptor(EventInterceptor eventInterceptor) {

    }

    @Override
    public void addDisconnectListener(DisconnectListener listener) {

    }

    @Override
    public void addConnectListener(ConnectListener listener) {

    }

    @Override
    public void addPingListener(PingListener listener) {

    }

    @Override
    public void addListeners(Object listeners) {

    }

    @Override
    public void addListeners(Object listeners, Class<?> listenersClass) {

    }

    @Override
    public void removeAllListeners(String eventName) {

    }
}
