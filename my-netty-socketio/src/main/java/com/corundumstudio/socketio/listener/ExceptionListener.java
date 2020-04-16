package com.corundumstudio.socketio.listener;

import com.corundumstudio.socketio.SocketIOClient;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public interface ExceptionListener {

    void onEventException(Exception e, List<Object> args, SocketIOClient client);

    void onDisconnectException(Exception e, SocketIOClient client);

    void onConnectException(Exception e, SocketIOClient client);

    void onPingException(Exception e, SocketIOClient client);

    boolean exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception;
}
