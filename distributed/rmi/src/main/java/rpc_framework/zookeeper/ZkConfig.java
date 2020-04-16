package rpc_framework.zookeeper;

public class ZkConfig {

    public static final String CONNECT_STR = "192.168.50.144:2181," +
            "192.168.50.144:2182," +
            "192.168.50.144:2183";

    public static final String REGISTER_PATH = "/registers";

    public static final int CONNECTION_TIMEOUTMS = 30000;

    public static final int SESSION_TIMEOUTMS = 5000;
}
