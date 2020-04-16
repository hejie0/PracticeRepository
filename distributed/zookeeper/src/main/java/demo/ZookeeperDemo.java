package demo;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * 原生zookeeper API使用
 */
public class ZookeeperDemo {

    private static String persistentPath = "/persistentNode"; //原生zookeeper的api必须先创建父节点才能创建子节点

    /**
     * 通过ZooKeeper对象操作节点及数据
     * @param zooKeeper
     */
    private static void crudNode(ZooKeeper zooKeeper) throws KeeperException, InterruptedException {
        //创建节点  参数: 节点路径，节点数据，节点访问权限，节点类型(持久)
        zooKeeper.create(persistentPath, "my is persistent node".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        //获取节点数据
        Stat stat = new Stat();
        byte[] data = zooKeeper.getData(persistentPath, null, stat);
        System.out.println(new String(data));

        //修改节点数据
        zooKeeper.setData(persistentPath, "my is persistent node00".getBytes(), stat.getVersion());
        //获取节点数据
        byte[] modData = zooKeeper.getData(persistentPath, null, stat);
        System.out.println(new String(modData));

        //删除节点
        zooKeeper.delete(persistentPath, stat.getVersion());
    }

    /**
     * 通过这3个方法绑定事件 getData、getChildren、exists
     * 绑定事件是一次性的，使用一次后需要重新绑定
     * 触发事件可以通过：create/delete/setData
     * @param zooKeeper
     */
    private static void buildEvent(ZooKeeper zooKeeper) throws KeeperException, InterruptedException {
        //zooKeeper.exists(persistentPath, true);  //true表示触发默认Watcher事件，也就是new ZooKeeper(watcher)
        Watcher watcher = new Watcher(){
            @Override
            public void process(WatchedEvent event) {
                //type类型可以点进event.getType()查看
                System.out.println("path: " + event.getPath() + ", type: " + event.getType() + ", State: " + event.getState());
                try {
                    zooKeeper.exists(persistentPath, this); //循环绑定事件，调用完后继续绑定
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        zooKeeper.getChildren("/", watcher);  //给所有子节点绑定事件
        //zooKeeper.exists(persistentPath, watcher); //存在则绑定事件
    }

    public static void main(String[] args) {

        try {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            String connectString = "192.168.50.144:2181," +
                    "192.168.50.144:2182," +
                    "192.168.50.144:2183";
            ZooKeeper zooKeeper = new ZooKeeper(connectString, 4000, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    //判断当前连接状态是否为连接成功
                    if (Event.KeeperState.SyncConnected == event.getState()){
                        System.out.println("当前连接状态" + event.getState());
                        countDownLatch.countDown();
                    }
                }
            });
            countDownLatch.await();

            buildEvent(zooKeeper); //绑定事件
            crudNode(zooKeeper); //操作zookeeper

            zooKeeper.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
