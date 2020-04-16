package com.corundumstudio.socketio;

import com.corundumstudio.socketio.handler.SuccessAuthorizationListener;
import com.corundumstudio.socketio.listener.DefaultExceptionListener;
import com.corundumstudio.socketio.listener.ExceptionListener;
import com.corundumstudio.socketio.protocol.JsonSupport;
import com.corundumstudio.socketio.store.MemoryStoreFactory;
import com.corundumstudio.socketio.store.StoreFactory;

import javax.net.ssl.KeyManagerFactory;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class Configuration {

    private ExceptionListener exceptionListener = new DefaultExceptionListener();

    private String context = "/socket.io";

    private List<Transport> transports = Arrays.asList(Transport.WEBSOCKET, Transport.POLLING);

    private int bossThreads = 0; // 0 = current_processors_amount * 2
    private int workerThreads = 0; // 0 = current_processors_amount * 2
    private boolean useLinuxNativeEpoll;

    private boolean allowCustomRequests = false;

    private int upgradeTimeout = 10000;
    private int pingTimeout = 60000;
    private int pingInterval = 25000;
    private int firstDataTimeout = 5000;

    private int maxHttpContentLength = 64 * 1024;
    private int maxFramePayloadLength = 64 * 1024;

    private String packagePrefix;
    private String hostname;
    private int port = -1;

    private String sslProtocol = "TLSv1";

    private String keyStoreFormat = "JKS";
    private InputStream keyStore;
    private String keyStorePassword;

    private String trustStoreFormat = "JKS";
    private InputStream trustStore;
    private String trustStorePassword;

    private String keyManagerFactoryAlgorithm = KeyManagerFactory.getDefaultAlgorithm();

    private boolean preferDirectBuffer = true;

    private SocketConfig socketConfig = new SocketConfig();

    private StoreFactory storeFactory = new MemoryStoreFactory();

    private JsonSupport jsonSupport;

    private AuthorizationListener authorizationListener = new SuccessAuthorizationListener();

    private AckMode ackMode = AckMode.AUTO_SUCCESS_ONLY;

    private boolean addVersionHeader = true;

    private String origin;

    private boolean httpCompression = true;

    private boolean websocketCompression = true;

    private boolean randomSession = false;

    public Configuration() {
    }

    /**
     * 防止进一步的修改克隆
     * Defend from further modifications by cloning
     *
     * @param conf Configuration object to clone
     */
    Configuration(Configuration conf) {
        setBossThreads(conf.getBossThreads());
        setWorkerThreads(conf.getWorkerThreads());
        setUseLinuxNativeEpoll(conf.isUseLinuxNativeEpoll());

        setPingInterval(conf.getPingInterval());
        setPingTimeout(conf.getPingTimeout());

        setHostname(conf.getHostname());
        setPort(conf.getPort());

        if (conf.getJsonSupport() == null) {
            try {
                getClass().getClassLoader().loadClass("com.fasterxml.jackson.databind.ObjectMapper");
                try {
                    Class<?> jjs = getClass().getClassLoader().loadClass("com.corundumstudio.socketio.protocol.JacksonJsonSupport");
                    JsonSupport js = (JsonSupport) jjs.getConstructor().newInstance();
                    conf.setJsonSupport(js);
                } catch (Exception e) {
                    throw new IllegalArgumentException(e);
                }
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Can't find jackson lib in classpath", e);
            }
        }

        setJsonSupport(new JsonSupportWrapper(conf.getJsonSupport()));
        setContext(conf.getContext());
        setAllowCustomRequests(conf.isAllowCustomRequests());

        setKeyStorePassword(conf.getKeyStorePassword());
        setKeyStore(conf.getKeyStore());
        setKeyStoreFormat(conf.getKeyStoreFormat());
        setTrustStore(conf.getTrustStore());
        setTrustStoreFormat(conf.getTrustStoreFormat());
        setTrustStorePassword(conf.getTrustStorePassword());
        setKeyManagerFactoryAlgorithm(conf.getKeyManagerFactoryAlgorithm());

        setTransports(conf.getTransports().toArray(new Transport[conf.getTransports().size()]));
        setMaxHttpContentLength(conf.getMaxHttpContentLength());
        setPackagePrefix(conf.getPackagePrefix());

        setPreferDirectBuffer(conf.isPreferDirectBuffer());
        setStoreFactory(conf.getStoreFactory());
        setAuthorizationListener(conf.getAuthorizationListener());
        setExceptionListener(conf.getExceptionListener());
        setSocketConfig(conf.getSocketConfig());
        setAckMode(conf.getAckMode());
        setMaxFramePayloadLength(conf.getMaxFramePayloadLength());
        setUpgradeTimeout(conf.getUpgradeTimeout());

        setAddVersionHeader(conf.isAddVersionHeader());
        setOrigin(conf.getOrigin());
        setSslProtocol(conf.getSslProtocol());

        setHttpCompression(conf.isHttpCompression());
        setWebsocketCompression(conf.isWebsocketCompression());
        setRandomSession(conf.isRandomSession());
    }

    public ExceptionListener getExceptionListener() {
        return exceptionListener;
    }

    /**
     * 异常监听器在SocketIO监听器中的任何异常上调用
     * Exception listener invoked on any exception in SocketIO listener
     *
     * @param exceptionListener - listener
     * @see com.corundumstudio.socketio.listener.ExceptionListener
     */
    public void setExceptionListener(ExceptionListener exceptionListener) {
        this.exceptionListener = exceptionListener;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public List<Transport> getTransports() {
        return transports;
    }

    /**
     * 服务器支持的传输
     * Transports supported by server
     * @param transports - list of transports
     */
    public void setTransports(Transport... transports) {
        if(transports.length == 0){
            throw new IllegalArgumentException("Transport list can't be empty");
        }
        this.transports = Arrays.asList(transports);
    }

    public int getBossThreads() {
        return bossThreads;
    }

    public void setBossThreads(int bossThreads) {
        this.bossThreads = bossThreads;
    }

    public int getWorkerThreads() {
        return workerThreads;
    }

    public void setWorkerThreads(int workerThreads) {
        this.workerThreads = workerThreads;
    }

    public boolean isUseLinuxNativeEpoll() {
        return useLinuxNativeEpoll;
    }

    public void setUseLinuxNativeEpoll(boolean useLinuxNativeEpoll) {
        this.useLinuxNativeEpoll = useLinuxNativeEpoll;
    }

    public boolean isAllowCustomRequests() {
        return allowCustomRequests;
    }

    /**
     * 允许服务自定义请求不同于套接字。io协议。
     * 在这种情况下，有必要添加自己的处理程序来处理它们避免挂起连接
     * 默认为false
     * Allow to service custom requests differs from socket.io protocol.
     * In this case it's necessary to add own handler which handle them
     * to avoid hang connections.
     * Default is {@code false}
     *
     * @param allowCustomRequests - {@code true} to allow
     */
    public void setAllowCustomRequests(boolean allowCustomRequests) {
        this.allowCustomRequests = allowCustomRequests;
    }

    public int getUpgradeTimeout() {
        return upgradeTimeout;
    }

    /**
     * 传输升级超时(毫秒)
     * Transport upgrade timeout in milliseconds
     * @param upgradeTimeout - upgrade timeout
     */
    public void setUpgradeTimeout(int upgradeTimeout) {
        this.upgradeTimeout = upgradeTimeout;
    }

    public int getPingTimeout() {
        return pingTimeout;
    }

    /**
     * Ping timeout
     * Use <code>0</code> to disable it
     *
     * @param heartbeatTimeoutSecs - time in milliseconds
     */
    public void setPingTimeout(int heartbeatTimeoutSecs) {
        this.pingTimeout = heartbeatTimeoutSecs;
    }

    public int getPingInterval() {
        return pingInterval;
    }

    /**
     * Ping interval
     * @param heartbeatIntervalSecs - time in milliseconds
     */
    public void setPingInterval(int heartbeatIntervalSecs) {
        this.pingInterval = heartbeatIntervalSecs;
    }

    public int getFirstDataTimeout() {
        return firstDataTimeout;
    }

    /**
     * 通道打开和第一次数据传输之间的超时有助于避免“沉默渠道”的攻击和预防“打开文件太多”的问题
     * Timeout between channel opening and first data transfer
     * Helps to avoid 'silent channel' attack and prevents
     * 'Too many open files' problem in this case
     *
     * @param firstDataTimeout - timeout value
     */
    public void setFirstDataTimeout(int firstDataTimeout) {
        this.firstDataTimeout = firstDataTimeout;
    }

    public int getMaxHttpContentLength() {
        return maxHttpContentLength;
    }

    /**
     * 设置最大的http内容长度限制
     * Set maximum http content length limit
     * @param maxHttpContentLength
     *        聚合的http内容的最大长度。
     *        the maximum length of the aggregated http content.
     */
    public void setMaxHttpContentLength(int maxHttpContentLength) {
        this.maxHttpContentLength = maxHttpContentLength;
    }

    public int getMaxFramePayloadLength() {
        return maxFramePayloadLength;
    }

    /**
     * 设置最大websocket框架内容长度限制
     * Set maximum websocket frame content length limit
     * @param maxFramePayloadLength - length
     */
    public void setMaxFramePayloadLength(int maxFramePayloadLength) {
        this.maxFramePayloadLength = maxFramePayloadLength;
    }

    public String getPackagePrefix() {
        return packagePrefix;
    }

    /**
     * 包前缀，用于从客户端发送不包含完整类名的json-object。
     * Package prefix for sending json-object from client without full class name.
     *
     * With defined package prefix socket.io client
     * just need to define '@class: 'SomeType'' in json object
     * instead of '@class: 'com.full.package.name.SomeType''
     *
     * @param packagePrefix - prefix string
     */
    public void setPackagePrefix(String packagePrefix) {
        this.packagePrefix = packagePrefix;
    }

    public String getHostname() {
        return hostname;
    }

    /**
     * 可选参数。如果未设置，则绑定地址将为0.0.0.0或::0
     * Optional parameter. If not set then bind address will be 0.0.0.0 or ::0
     * @param hostname - name of host
     */
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getSslProtocol() {
        return sslProtocol;
    }

    /**
     * 设置请求的SSL协议的名称
     * Set the name of the requested SSL protocol
     * @param sslProtocol - name of protocol
     */
    public void setSslProtocol(String sslProtocol) {
        this.sslProtocol = sslProtocol;
    }

    public String getKeyStoreFormat() {
        return keyStoreFormat;
    }

    /**
     * 密钥存储格式
     * Key store format
     * @param keyStoreFormat - key store format
     */
    public void setKeyStoreFormat(String keyStoreFormat) {
        this.keyStoreFormat = keyStoreFormat;
    }

    public InputStream getKeyStore() {
        return keyStore;
    }

    /**
     * SSL密钥存储流，可以指定给任何源
     * SSL key store stream, maybe appointed to any source
     *
     * @param keyStore - key store input stream
     */
    public void setKeyStore(InputStream keyStore) {
        this.keyStore = keyStore;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    /**
     * SSL密钥存储密码
     * SSL key store password
     *
     * @param keyStorePassword - password of key store
     */
    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public String getTrustStoreFormat() {
        return trustStoreFormat;
    }

    public void setTrustStoreFormat(String trustStoreFormat) {
        this.trustStoreFormat = trustStoreFormat;
    }

    public InputStream getTrustStore() {
        return trustStore;
    }

    public void setTrustStore(InputStream trustStore) {
        this.trustStore = trustStore;
    }

    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    public void setTrustStorePassword(String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
    }

    public String getKeyManagerFactoryAlgorithm() {
        return keyManagerFactoryAlgorithm;
    }

    public void setKeyManagerFactoryAlgorithm(String keyManagerFactoryAlgorithm) {
        this.keyManagerFactoryAlgorithm = keyManagerFactoryAlgorithm;
    }

    public boolean isPreferDirectBuffer() {
        return preferDirectBuffer;
    }

    /**
     * 包编码过程中使用的缓冲区分配方法。
     * Buffer allocation method used during packet encoding.
     * Default is {@code true}
     *
     * @param preferDirectBuffer    {@code true} if a direct buffer should be tried to be used as target for
     *                              the encoded messages. If {@code false} is used it will allocate a heap
     *                              buffer, which is backed by an byte array.
     */
    public void setPreferDirectBuffer(boolean preferDirectBuffer) {
        this.preferDirectBuffer = preferDirectBuffer;
    }

    public SocketConfig getSocketConfig() {
        return socketConfig;
    }

    /**
     * 配置TCP套接字
     * TCP socket configuration
     * @param socketConfig - config
     */
    public void setSocketConfig(SocketConfig socketConfig) {
        this.socketConfig = socketConfig;
    }

    public StoreFactory getStoreFactory() {
        return storeFactory;
    }

    /**
     * 数据存储—用于存储会话数据并实现分布式pubsub。
     * Data store - used to store session data and implements distributed pubsub.
     * Default is {@code MemoryStoreFactory}
     *
     * @param clientStoreFactory - implements StoreFactory
     *
     * @see com.corundumstudio.socketio.store.MemoryStoreFactory
     * @see com.corundumstudio.socketio.store.RedissonStoreFactory
     * @see com.corundumstudio.socketio.store.HazelcastStoreFactory
     */
    public void setStoreFactory(StoreFactory clientStoreFactory) {
        this.storeFactory = clientStoreFactory;
    }

    public JsonSupport getJsonSupport() {
        return jsonSupport;
    }

    /**
     * 允许设置自定义实现的JSON序列化/反序列化
     * Allows to setup custom implementation of JSON serialization/deserialization
     * @param jsonSupport - json mapper
     * @see JsonSupport
     */
    public void setJsonSupport(JsonSupport jsonSupport) {
        this.jsonSupport = jsonSupport;
    }

    public AuthorizationListener getAuthorizationListener() {
        return authorizationListener;
    }

    /**
     * 在每次握手时调用授权侦听器。
     * 通过AuthorizationListener接受或拒绝客户端 isAuthorized方法。
     * 默认接受所有客户端
     *
     * Authorization listener invoked on every handshake.
     * Accepts or denies a client by {@code AuthorizationListener.isAuthorized} method.
     * <b>Accepts</b> all clients by default.
     *
     * @param authorizationListener - authorization listener itself
     *
     * @see com.corundumstudio.socketio.AuthorizationListener
     */
    public void setAuthorizationListener(AuthorizationListener authorizationListener) {
        this.authorizationListener = authorizationListener;
    }

    public AckMode getAckMode() {
        return ackMode;
    }

    /**
     * Auto ack-response mode
     * Default is {@code AckMode.AUTO_SUCCESS_ONLY}
     * @see AckMode
     * @param ackMode - ack mode
     */
    public void setAckMode(AckMode ackMode) {
        this.ackMode = ackMode;
    }

    public boolean isAddVersionHeader() {
        return addVersionHeader;
    }

    /**
     * 添加服务器标头与lib版本的http响应。
     * Adds <b>Server</b> header with lib version to http response.
     * <p>
     * Default is <code>true</code>
     *
     * @param addVersionHeader - <code>true</code> to add header
     */
    public void setAddVersionHeader(boolean addVersionHeader) {
        this.addVersionHeader = addVersionHeader;
    }

    public String getOrigin() {
        return origin;
    }

    /**
     * 为http的每个响应设置访问控制-允许源标头值。
     * Set <b>Access-Control-Allow-Origin</b> header value for http each response.
     * Default is <code>null</code>
     *
     * If value is <code>null</code> then request <b>ORIGIN</b> header value used.
     * @param origin - origin
     */
    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public boolean isHttpCompression() {
        return httpCompression;
    }

    /**
     * Activate http protocol compression. Uses {@code gzip} or
     * {@code deflate} encoding choice depends on the {@code "Accept-Encoding"} header value.
     * <p>
     * Default is <code>true</code>
     *
     * @param httpCompression - <code>true</code> to use http compression
     */
    public void setHttpCompression(boolean httpCompression) {
        this.httpCompression = httpCompression;
    }

    public boolean isWebsocketCompression() {
        return websocketCompression;
    }

    /**
     * 激活websocket协议压缩。
     * Activate websocket protocol compression.
     * Uses {@code permessage-deflate} encoding only.
     * <p>
     * Default is <code>true</code>
     *
     * @param websocketCompression - <code>true</code> to use websocket compression
     */
    public void setWebsocketCompression(boolean websocketCompression) {
        this.websocketCompression = websocketCompression;
    }

    public boolean isRandomSession() {
        return randomSession;
    }

    public void setRandomSession(boolean randomSession) {
        this.randomSession = randomSession;
    }
}
