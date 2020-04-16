package com.corundumstudio.socketio;

import com.corundumstudio.socketio.handler.ClientHead;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public class SocketIOChannelInitializer extends ChannelInitializer<Channel> implements DisconnectableHub {
    @Override
    public void onDisconnect(ClientHead client) {

    }

    @Override
    protected void initChannel(Channel channel) throws Exception {

    }
}
