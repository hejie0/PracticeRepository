package rpc_framework.zookeeper.server;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import rpc_framework.zookeeper.ZkConfig;

public class RegisterCenterImpl implements IRegisterCenter {

    private CuratorFramework curatorFramework;

    public RegisterCenterImpl(String connectStr){
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(connectStr)
                .connectionTimeoutMs(ZkConfig.CONNECTION_TIMEOUTMS)
                .sessionTimeoutMs(ZkConfig.SESSION_TIMEOUTMS)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        curatorFramework.start();
    }

    @Override
    public void register(String serviceName, String serviceAddress) {
        //注册相应的服务
        String servicePath = ZkConfig.REGISTER_PATH  + "/" + serviceName;
        try {
            if(curatorFramework.checkExists().forPath(servicePath) == null){
                curatorFramework.create().creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath(servicePath, "0".getBytes());
            }

            String addressPath = servicePath + "/" + serviceAddress;
            String rsNode = curatorFramework.create().creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(addressPath, "0".getBytes());

            System.out.println("服务注册成功: " + rsNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
