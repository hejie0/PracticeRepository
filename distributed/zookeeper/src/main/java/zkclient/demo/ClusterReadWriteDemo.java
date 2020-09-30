package zkclient.demo;

import org.I0Itec.zkclient.ZkClient;

import java.util.Random;

public class ClusterReadWriteDemo {

	public static void main(String[] args) {
		// 创建一个zk客户端
		ZkClient client = new ZkClient("localhost:2182,localhost:2183,localhost:2184");
		client.setZkSerializer(new MyZkSerializer());
		client.createPersistent("/zk/a", true);
		
		// 不停的写数据
		new Thread(()->{
			while(true) {
				try {
					int value = (new Random()).nextInt(50000);
					client.writeData("/zk/a", value);
					System.out.println("修改/zk/a的值为："+value);
					Thread.sleep(5000L);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		
		// 不停的读数据
		new Thread(()->{
			while(true) {
				String data = client.readData("/zk/a");
				System.out.println("读取到了/zk/a节点数据内容："+data);
				try {
					Thread.sleep(5000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();

		try {
			Thread.currentThread().join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
