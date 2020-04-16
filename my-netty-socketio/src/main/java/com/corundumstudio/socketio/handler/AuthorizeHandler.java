package com.corundumstudio.socketio.handler;

import com.corundumstudio.socketio.Disconnectable;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;

@ChannelHandler.Sharable
public class AuthorizeHandler extends ChannelInboundHandlerAdapter implements Disconnectable {

    public void onDisconnect(ClientHead client) {

    }
}
