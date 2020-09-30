package zkclient.lock;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import zkclient.demo.MyZkSerializer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 分布式锁的实现，类似非公平锁
 *
 */
public class ZKDistributeLock implements Lock {
	// Znode锁节点 /zk/lock
	private String lockPath;
	// zookeeper客户端
	private ZkClient client;

	public ZKDistributeLock(String lockPath) {
		if(lockPath ==null || lockPath.trim().equals("")) {
			throw new IllegalArgumentException("patch不能为空字符串");
		}
		this.lockPath = lockPath;
		
		client = new ZkClient("localhost:2181");
		client.setZkSerializer(new MyZkSerializer());
	}

	@Override
	public boolean tryLock() { // 不会阻塞
		// 抢锁，成功的创建节点
		try {
			client.createEphemeral(lockPath);
		}catch (ZkNodeExistsException e) {
			return false; // 节点已经被其他请求创建了，强锁失败
		}
		return true;
	}
	
	@Override
	public void unlock() {
		// 释放锁，删除节点
		client.delete(lockPath);
	}
	
	// 一直获取锁，直达获得锁
	@Override
	public void lock() { // 如果获取不到锁，阻塞等待
		// 去抢锁，试着
		if(tryLock()) {
			// 成了，执行业务逻辑，跳出方法。
		}else {
			// 阻塞等待
			waitForLock();
			// 要重新抢锁
			lock();
		}
	}
	
	private void waitForLock() {
		// 涉及到多线程里面的内容
		CountDownLatch cdl = new CountDownLatch(1);
		// 用zkwatcher事件来通知
		IZkDataListener listener = new IZkDataListener() {
			@Override
			public void handleDataDeleted(String dataPath) throws Exception {
				System.out.println("================zk节点被删除================");
				cdl.countDown();
			}
			@Override
			public void handleDataChange(String dataPath, Object data) throws Exception {
			}
		};
		// watcher /zk 数据变化
		client.subscribeDataChanges(lockPath, listener);
		
		// 请求线程去进行阻塞等待
		if(client.exists(lockPath)) {
			try {
				cdl.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// 取消注册事件
		client.unsubscribeDataChanges(lockPath, listener);
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean tryLock(long time, TimeUnit unit)
			throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Condition newCondition() {
		// TODO Auto-generated method stub
		return null;
	}

}
