package demo;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.TimeUnit;

/**
 * CuratorFramework是基于原生zookeeper API封装的一个框架，方便使用
 * 官方文档：http://curator.apache.org/getting-started.html
 */
public class CuratorFrameworkDemo {

    private static String persistentPath = "/persistentNode/node1";
    private static CuratorFramework curatorFramework;

    private static void initCuratorFramework(){
        String address = "192.168.50.144:2181," +
                "192.168.50.144:2182," +
                "192.168.50.144:2183";

        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(address)
                .connectionTimeoutMs(30000)
                .sessionTimeoutMs(50000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .namespace("curator").build();

        curatorFramework.start();
    }

    private static void closeCuratorFramework(){
        curatorFramework.close();
    }

    /**
     * 读取
     * curatorFramework.getData().forPath() 普通查询值
     * curatorFramework.getData().storingStatIn().forPath() 查询状态和值
     * 创建
     * curatorFramework.create().forPath() 默认持久节点
     * curatorFramework.create().withMode().forPath() 创建一个节点 CreateMode可选择节点类型
     * curatorFramework.create().creatingParentsIfNeeded()
     *    .withMode().forPath()  递归创建 CreateMode可选择节点类型
     * 更新
     * curatorFramework.setData().forPath() 普通更新值
     * curatorFramework.setData().withVersion().forPath() 指定版本更新值
     * 删除
     * curatorFramework.delete().forPath() 删除节点
     * curatorFramework.delete().deletingChildrenIfNeeded().forPath() 删除节点并递归删除其子节点
     * curatorFramework.delete().withVersion().forPath() 指定版本删除
     * curatorFramework.delete().deletingChildrenIfNeeded().withVersion().forPath() 指定版本递归删除
     * curatorFramework.delete().guaranteed().forPath() 强制保证删除一个节点
     * curatorFramework.delete().guaranteed().deletingChildrenIfNeeded().forPath() 强制保证递归删除节点及子节点
     *
     * @param curatorFramework
     * @throws Exception
     */
    public static void crudNode(CuratorFramework curatorFramework) throws Exception {
        //结果：/curator/persistentNode/node1

        /* 异步创建
        curatorFramework.create().withMode(CreateMode.EPHEMERAL).inBackground(new BackgroundCallback() {
            public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
                System.out.println("当前线程：" + Thread.currentThread().getName() + ",code:"
                        + event.getResultCode() + ",type:" + event.getType());
            }
        }, Executors.newFixedThreadPool(10)).forPath(persistentPath);

        事务操作（curator独有的）
        CuratorTransactionResult.getType() getForPath() getResultPath() getResultStat()
        创建节点persistentPath 并且 修改一个不存在的节点 /abc，commit会失败
        Collection<CuratorTransactionResult> curatorTransactionResults = curatorFramework.inTransaction()
                .create().forPath(persistentPath, "123".getBytes()).and()
                .setData().forPath("/abc", "123".getBytes()).and().commit();
        */
        //创建节点
        curatorFramework.create().creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .forPath(persistentPath, "123".getBytes());
        //获取数据
        Stat state = new Stat();
        byte[] data = curatorFramework.getData().storingStatIn(state).forPath(persistentPath);
        System.out.println(new String(data));

        //修改数据
        curatorFramework.setData().forPath(persistentPath, "321".getBytes());
        //获取数据
        data = curatorFramework.getData().storingStatIn(state).forPath(persistentPath);
        System.out.println(new String(data));

        //删除节点
        curatorFramework.delete().guaranteed().deletingChildrenIfNeeded().forPath(persistentPath);
    }

    /**
     * PathChildrenCache 监听一个节点下子节点的create、delete、set
     * NodeCache    监听一个节点的create、set
     * TreeCache    综合PathChildCache和NodeCache的特性，还会缓存路径下所有子节点的数据
     */
    public static void addListenerWithPathChildCache(CuratorFramework curatorFramework, String path) throws Exception {
        PathChildrenCache pathChildrenCache = new PathChildrenCache(curatorFramework, path, false);
        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                System.out.println("PathChildrenCache: " + event.getType());
            }
        });
        pathChildrenCache.start(PathChildrenCache.StartMode.NORMAL);
    }

    public static void addListenerWithNodeCache(CuratorFramework curatorFramework, String path) throws Exception {
        NodeCache nodeCache = new NodeCache(curatorFramework, path, false);
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                System.out.println("NodeCache: " + nodeCache.getCurrentData().getPath());
            }
        });
        nodeCache.start();
    }

    public static void addListenerWithTreeCache(CuratorFramework curatorFramework, String path) throws Exception {
        TreeCache treeCache = new TreeCache(curatorFramework, path);
        treeCache.getListenable().addListener(new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
                System.out.println("TreeCache: " + event.getType());
            }
        });
        treeCache.start();
    }

    private static void leaderSelector(CuratorFramework curatorFramework, String masterPath) {
        LeaderSelector leaderSelector = new LeaderSelector(curatorFramework, masterPath, new LeaderSelectorListenerAdapter() {
            @Override
            public void takeLeadership(CuratorFramework client) throws Exception {
                System.out.println(client.getZookeeperClient().getZooKeeper().getSessionId() + ": 获得leader成功");
                TimeUnit.SECONDS.sleep(2);
            }
        });
        leaderSelector.autoRequeue();
        leaderSelector.start();
    }

    private static void testLeaderSelector(String masterPath) throws InterruptedException {
        initCuratorFramework();
        leaderSelector(curatorFramework, masterPath);
        initCuratorFramework();
        leaderSelector(curatorFramework, masterPath);
        initCuratorFramework();
        leaderSelector(curatorFramework, masterPath);
        TimeUnit.SECONDS.sleep(30);
    }

    public static void main(String[] args) throws Exception {
        initCuratorFramework();
        //addListenerWithPathChildCache(curatorFramework, "/persistentNode"); //绑定PathChildCache监听事件
        //addListenerWithNodeCache(curatorFramework, "/persistentNode/node1"); //绑定NodeCache监听事件
        addListenerWithTreeCache(curatorFramework, "/persistentNode/node1"); //绑定TreeCache监听事件
        crudNode(curatorFramework);
        closeCuratorFramework();

        testLeaderSelector("/curator_master_path");
    }

}
