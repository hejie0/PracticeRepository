package com.corundumstudio.socketio.listener;

import com.corundumstudio.socketio.SocketIOClient;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

/**
 * 基本回调异常监听器
 * Base callback exceptions listener
 */
public class ExceptionListenerAdapter implements ExceptionListener {

    public void onEventException(Exception e, List<Object> args, SocketIOClient client) {

    }

    public void onDisconnectException(Exception e, SocketIOClient client) {

    }

    public void onConnectException(Exception e, SocketIOClient client) {

    }

    public void onPingException(Exception e, SocketIOClient client) {

    }

    public boolean exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
        return false;
    }
}
