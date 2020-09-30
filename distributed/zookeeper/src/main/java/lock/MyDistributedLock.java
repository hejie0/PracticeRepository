package lock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 基于原生Zookeeper实现公平锁
 * https://www.cnblogs.com/qlqwjy/p/10518900.html 锁
 * https://www.iteye.com/blog/z63as-2432750 zk日志
 */
public class MyDistributedLock implements Lock, Watcher {

    String connectString = "192.168.50.144:2181," +
            "192.168.50.144:2182," +
            "192.168.50.144:2183";
    private ZooKeeper zooKeeper;
    private String ROOT_LOCK = "/locks"; //定义根节点
    private String WAIT_LOCK; //等待前一个锁
    private String CURRENT_LOCK; //表示当前的锁

    private CountDownLatch countDownLatch;

    public MyDistributedLock() throws Exception {
        zooKeeper = new ZooKeeper(connectString, 4000, this);
        Stat stat = zooKeeper.exists(ROOT_LOCK, false);
        if (stat == null){
            zooKeeper.create(ROOT_LOCK, "0".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }

    @Override
    public boolean tryLock() {
        try {
            //创建临时有序节点
            CURRENT_LOCK = zooKeeper.create(ROOT_LOCK + "/", "0".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            System.out.println(Thread.currentThread().getName() + "-->" + CURRENT_LOCK + "尝试竞争锁");

            List<String> childrens = zooKeeper.getChildren(ROOT_LOCK, false); //获取子节点
            TreeSet<String> sortedSet = new TreeSet(childrens); //将子节点进行排序
            SortedSet<String> lessThenMe = sortedSet.headSet(CURRENT_LOCK); //返回比当前节点小的节点
            String firstNode = sortedSet.first();
            if(CURRENT_LOCK.equals(firstNode)){ //通过当前的节点和集合中最小的节点进行比较，相等就获得锁
                return true;
            }
            if (!lessThenMe.isEmpty()){
                WAIT_LOCK = lessThenMe.last(); //获取比当前节点 更小的最后一个节点，设置给WAIT_LOCK
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void lock() {
        if(this.tryLock()){ //如果获取锁成功
            System.out.println(Thread.currentThread().getName() + "-->" + CURRENT_LOCK + "获得锁成功");
        }

        try {
            waitForLock(WAIT_LOCK); //没有获得锁，继续等待获得锁
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void waitForLock(String prev) throws KeeperException, InterruptedException {
        Stat stat = zooKeeper.exists(prev, true); //监听当前节点的上一个节点
        if(stat != null){
            System.out.println(Thread.currentThread().getName() + "-->等待锁" + ROOT_LOCK.concat(prev) + "释放");
            countDownLatch = new CountDownLatch(1);
            countDownLatch.await();
            System.out.println(Thread.currentThread().getName() + "-->获得锁成功");
        }
    }

    @Override
    public void process(WatchedEvent event) {
        if (this.countDownLatch != null && event.getType() == Event.EventType.NodeDeleted){
            this.countDownLatch.countDown();
        }
    }

    @Override
    public void unlock() {
        System.out.println(Thread.currentThread().getName() + "-->释放锁" + CURRENT_LOCK);
        try {
            zooKeeper.delete(CURRENT_LOCK, -1); // -1表示：不管是什么版本都删除
            CURRENT_LOCK = null;
            zooKeeper.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public Condition newCondition() {
        return null;
    }

}
