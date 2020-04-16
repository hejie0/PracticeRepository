package rpc_framework.zookeeper.client;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import rpc_framework.zookeeper.ZkConfig;
import rpc_framework.zookeeper.client.loadbanalce.LoadBanalce;
import rpc_framework.zookeeper.client.loadbanalce.RandomLoadBanalce;

import java.util.List;

public class ServiceDiscoveryImpl implements IServiceDiscovery {

    private CuratorFramework curatorFramework;
    private List<String> repos;

    public ServiceDiscoveryImpl(String connectStr){
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(connectStr)
                .connectionTimeoutMs(ZkConfig.CONNECTION_TIMEOUTMS)
                .sessionTimeoutMs(ZkConfig.SESSION_TIMEOUTMS)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        curatorFramework.start();
    }

    @Override
    public String discovery(String serviceName) {
        String path = ZkConfig.REGISTER_PATH  + "/" + serviceName;
        try {
            repos = curatorFramework.getChildren().forPath(path);
        } catch (Exception e) {
           throw new RuntimeException("获取子节点异常: " + e);
        }
        //动态发现服务节点的变化
        registerWatcher(path);

        //负载均衡机制
        LoadBanalce loadBanalce = new RandomLoadBanalce();
        return loadBanalce.selectHost(repos);
    }

    private void registerWatcher(String path) {
        PathChildrenCache pathChildrenCache = new PathChildrenCache(curatorFramework, path, false);
        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                repos = curatorFramework.getChildren().forPath(path);
            }
        });
        try {
            pathChildrenCache.start(PathChildrenCache.StartMode.NORMAL);
        } catch (Exception e) {
            throw new RuntimeException("注册监听事件异常: " + e);
        }
    }
}
