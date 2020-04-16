package com.corundumstudio.socketio.listener;

import com.corundumstudio.socketio.SocketIOClient;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DefaultExceptionListener extends ExceptionListenerAdapter {

    private static final Logger log = LoggerFactory.getLogger(DefaultExceptionListener.class);

    @Override
    public void onEventException(Exception e, List<Object> args, SocketIOClient client) {
        log.error(e.getMessage(), e);
    }

    @Override
    public void onDisconnectException(Exception e, SocketIOClient client) {
        log.error(e.getMessage(), e);
    }

    @Override
    public void onConnectException(Exception e, SocketIOClient client) {
        log.error(e.getMessage(), e);
    }

    @Override
    public void onPingException(Exception e, SocketIOClient client) {
        log.error(e.getMessage(), e);
    }

    @Override
    public boolean exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
        log.error(e.getMessage(), e);
        return true;
    }
}
