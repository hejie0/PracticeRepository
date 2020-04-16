package com.corundumstudio.socketio;

public interface AuthorizationListener {

    /**
     * 检查客户端是否已授权握手数据
     * Checks is client with handshake data is authorized
     * @param data handshake data
     *             如果true客户被授权,否则false
     * @return <b>true</b> if client is authorized of <b>false</b> otherwise
     */
    boolean isAuthorized(HandshakeData data);

}
