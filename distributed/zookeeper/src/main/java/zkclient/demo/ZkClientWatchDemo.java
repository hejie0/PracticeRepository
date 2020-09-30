package zkclient.demo;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.List;

/**
 * Watch机制示例代码
 * ZkClientWatchDemo
 */
public class ZkClientWatchDemo {

	public static void main(String[] args) {
		// 创建一个zk客户端
		ZkClient client = new ZkClient("192.168.100.11:2181,192.168.100.11:2182,192.168.100.11:2183");
		client.setZkSerializer(new MyZkSerializer());
		
		// 创建节点 /zk
		if(! client.exists("/zk")) {
			client.createPersistent("/zk", true);
		}
		
		// watcher /zk 数据变化
		client.subscribeDataChanges("/zk", new IZkDataListener() {

			@Override
			public void handleDataDeleted(String dataPath) throws Exception {
				System.out.println("----收到'"+dataPath+"'节点被删除了-------------");
			}

			@Override
			public void handleDataChange(String dataPath, Object data) throws Exception {
				System.out.println("----收到'"+dataPath+"'节点数据变化：" + data + "-------------");
			}
		});
		
		// watcher 子节点 变化
		client.subscribeChildChanges("/zk", new IZkChildListener() {
			
			@Override
			public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
				System.out.println("----收到'"+parentPath+"'子节点发生变化-------------"+currentChilds);
			}
		});

		try {
			Thread.currentThread().join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
