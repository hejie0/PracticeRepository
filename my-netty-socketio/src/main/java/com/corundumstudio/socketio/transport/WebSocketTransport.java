package com.corundumstudio.socketio.transport;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class WebSocketTransport extends ChannelInboundHandlerAdapter {

    public static final String NAME = "websocket";

    private static final Logger log = LoggerFactory.getLogger(WebSocketTransport.class);

}
