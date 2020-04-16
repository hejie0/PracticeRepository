package lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 基于CuratorFramework实现 共享可重入锁 使用到的类是InterProcessMutex
 * 详情参考官方文档：http://curator.apache.org/curator-recipes/index.html
 */
public class CuratorDistributedLock {

    private static CuratorFramework curatorFramework;

    private static InterProcessMutex interProcessMutex;

    private static ExecutorService executorService;

    private static final String connectString = "192.168.50.144:2181," +
                                            "192.168.50.144:2182," +
                                            "192.168.50.144:2183";

    private static final String root = "/locks";

    private String lockName;

    public String getLockName() {
        return lockName;
    }

    public void setLockName(String lockName) {
        this.lockName = lockName;
    }

    static {
        curatorFramework = CuratorFrameworkFactory.builder().connectString(connectString).connectionTimeoutMs(5000).sessionTimeoutMs(5000).retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
        executorService = Executors.newCachedThreadPool();
        curatorFramework.start();
    }

    public CuratorDistributedLock(String lockName) {
        this.lockName = lockName;
        interProcessMutex = new InterProcessMutex(curatorFramework, root.concat(lockName));
    }

    /*上锁*/
    public void tryLock() {
        int count = 0;
        try {
            while (!interProcessMutex.acquire(1, TimeUnit.SECONDS)) {
                count++;
                if (count > 3) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*释放*/
    public void releaseLock() {
        try {
            if (interProcessMutex != null) {
                interProcessMutex.release();
            }
            curatorFramework.delete().inBackground(new BackgroundCallback() {
                @Override
                public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {

                }
            }, executorService).forPath(root.concat(lockName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
