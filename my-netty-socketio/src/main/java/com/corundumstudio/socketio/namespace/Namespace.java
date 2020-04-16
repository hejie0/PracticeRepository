package com.corundumstudio.socketio.namespace;

import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.annotation.ScannerEngine;
import com.corundumstudio.socketio.listener.*;
import com.corundumstudio.socketio.protocol.JsonSupport;
import com.corundumstudio.socketio.store.StoreFactory;
import com.corundumstudio.socketio.transport.NamespaceClient;
import io.netty.util.internal.PlatformDependent;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * 一个名称空间中所有客户端的集线器对象。
 * 由不同的名称空间客户机共享的名称空间。
 * <p>
 * Hub object for all clients in one namespace.
 * Namespace shares by different namespace-clients.
 */
public class Namespace implements SocketIONamespace {

    public static final String DEFAULT_NAME = "";

    private final ScannerEngine engine = new ScannerEngine();
    private final ConcurrentMap<String, EventEntry<?>> eventListeners = PlatformDependent.newConcurrentHashMap();
    private final Queue<ConnectListener> connectListeners = new ConcurrentLinkedQueue<>();
    private final Queue<DisconnectListener> disconnectListeners = new ConcurrentLinkedQueue<>();
    private final Queue<PingListener> pingListeners = new ConcurrentLinkedQueue<>();
    private final Queue<EventInterceptor> eventInterceptors = new ConcurrentLinkedQueue<>();

    private final Map<UUID, SocketIOClient> allClients = PlatformDependent.newConcurrentHashMap();
    private final ConcurrentMap<String, Set<UUID>> roomClients = PlatformDependent.newConcurrentHashMap();
    private final ConcurrentMap<UUID, Set<String>> clientRooms = PlatformDependent.newConcurrentHashMap();

    private final String name;
    private final AckMode ackMode;
    private final JsonSupport jsonSupport;
    private final StoreFactory storeFactory;
    private final ExceptionListener exceptionListener;

    public Namespace(String name, Configuration configuration) {
        this.name = name;
        this.ackMode = configuration.getAckMode();
        this.jsonSupport = configuration.getJsonSupport();
        this.storeFactory = configuration.getStoreFactory();
        this.exceptionListener = configuration.getExceptionListener();
    }

    public void addClient(SocketIOClient client) {

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public BroadcastOperations getBroadcastOperations() {
        return null;
    }

    @Override
    public BroadcastOperations getRoomOperations(String room) {
        return null;
    }

    @Override
    public Collection<SocketIOClient> getAllClients() {
        return null;
    }

    @Override
    public SocketIOClient getClient(UUID uuid) {
        return null;
    }

    @Override
    public void addMultiTypeEventListener(String eventName, MultiTypeEventListener listener, Class<?>... eventClass) {
        EventEntry entry = eventListeners.get(eventName);
        if (entry == null) {
            entry = new EventEntry();
            EventEntry oldEntry = eventListeners.putIfAbsent(eventName, entry);
            if (oldEntry != null) {
                entry = oldEntry;
            }
        }
        entry.addListener(listener);
        jsonSupport.addEventMapping(name, eventName, eventClass);
    }

    @Override
    public void removeAllListeners(String eventName) {
        EventEntry<?> entry = eventListeners.remove(eventName);
        if (entry != null) {
            jsonSupport.removeEventMapping(name, eventName);
        }
    }

    @Override
    public <T> void addEventListener(String eventName, Class<T> eventClass, DataListener<T> listener) {
        EventEntry entry = eventListeners.get(eventName);
        if (entry == null) {
            entry = new EventEntry<T>();
            EventEntry oldEntry = eventListeners.putIfAbsent(eventName, entry);
            if (oldEntry != null) {
                entry = oldEntry;
            }
        }
        entry.addListener(listener);
        jsonSupport.addEventMapping(name, eventName, eventClass);
    }

    @Override
    public void addEventInterceptor(EventInterceptor eventInterceptor) {
        eventInterceptors.add(eventInterceptor);
    }

    public void onEvent(NamespaceClient client, String eventName, List<Object> args, AckRequest ackRequest) {
        EventEntry entry = eventListeners.get(eventName);
        if (entry == null) {
            return;
        }

        try {
            Queue<DataListener> listeners = entry.getListeners();
            for (DataListener dataListener : listeners) {
                Object data = getEventData(args, dataListener);
                dataListener.onData(client, data, ackRequest);
            }

            for (EventInterceptor eventInterceptor : eventInterceptors) {
                eventInterceptor.onEvent(client, eventName, args, ackRequest);
            }
        } catch (Exception e) {
            exceptionListener.onEventException(e, args, client);
            if (ackMode == AckMode.AUTO_SUCCESS_ONLY) {
                return;
            }
        }

        sendAck(ackRequest);
    }

    private void sendAck(AckRequest ackRequest) {
        if (ackMode == AckMode.AUTO || ackMode == AckMode.AUTO_SUCCESS_ONLY) {
            // send ack response if it not executed
            // during {@link DataListener#onData} invocation
            ackRequest.sendAckData(Collections.emptyList());
        }
    }

    private Object getEventData(List<Object> args, DataListener<?> dataListener) {
        if (dataListener instanceof MultiTypeEventListener) {
            return new MultiTypeArgs(args);
        }else {
            if (!args.isEmpty()){
                return args.get(0);
            }
        }
        return null;
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

}
