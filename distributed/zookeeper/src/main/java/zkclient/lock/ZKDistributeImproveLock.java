package zkclient.lock;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import zkclient.demo.MyZkSerializer;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 升级版的分布式锁实现，类似公平锁。
 *
 */
public class ZKDistributeImproveLock implements Lock {
	/*
	 * 利用临时顺序节点来实现分布式锁
	 * 获取锁：取排队号（创建自己的临时顺序节点），
	 * 然后判断自己是否是最小号，如是，则获得锁；不是，则注册前一节点的watcher,阻塞等待
	 * 释放锁：删除自己创建的临时顺序节点
	 */
	// 父节点
	private String LockPath;
	private ZkClient client;
	
	// 自己号码、关注前面的节点号码
	private ThreadLocal<String> currentPath = new ThreadLocal<String>();
	private ThreadLocal<String> beforePath = new ThreadLocal<String>();

	public ZKDistributeImproveLock(String lockPath) {
		if(lockPath ==null || lockPath.trim().equals("")) {
			throw new IllegalArgumentException("patch不能为空字符串");
		}
		LockPath = lockPath;
		client = new ZkClient("localhost:2181");
		client.setZkSerializer(new MyZkSerializer());
		
		// 创建锁的父节点
		if (!this.client.exists(LockPath)) {
			try {
				this.client.createPersistent(LockPath);
			} catch (ZkNodeExistsException e) {

			}
		}
	}

	@Override
	public boolean tryLock() {
		// 初次进来才需要去号码
		if(currentPath.get() == null || !client.exists(currentPath.get())) {
			String node = client.createEphemeralSequential(LockPath+"/", "locked");
			currentPath.set(node);
		}
		
		// 获得所有的子
		List<String> children =client.getChildren(LockPath);
		// 排序list
		Collections.sort(children);
		
		// 判断当前节点是否是最小的
		if(currentPath.get().equals(LockPath+"/"+children.get(0))) {
			return true;
		}else {
			// 等前面的哥们
			int curIndex = children.indexOf(currentPath.get().substring(LockPath.length() + 1));
			String bnode = LockPath+"/"+children.get(curIndex -1);
			beforePath.set(bnode);
		}
		return false;
	}

	@Override
	public void lock() {
		if(! tryLock()) {
			// 阻塞等待锁的释放
			waitForLock();
			// 重新抢锁
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
		client.subscribeDataChanges(beforePath.get(), listener);
		
		// 请求线程去进行阻塞等待
		if(client.exists(beforePath.get())) {
			try {
				cdl.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// 取消注册事件
		client.unsubscribeDataChanges(beforePath.get(), listener);
	}

	@Override
	public void unlock() {
		if(currentPath.get() != null) {
			client.delete(currentPath.get());
			currentPath.set(null);
		}
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Condition newCondition() {
		// TODO Auto-generated method stub
		return null;
	}
}
