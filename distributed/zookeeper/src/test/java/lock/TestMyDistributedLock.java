package lock;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class TestMyDistributedLock {

    public static void main(String[] args) throws IOException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        for (int i = 0; i < 10; i++){
            new Thread(()->{
                try {
                    countDownLatch.await();
                    MyDistributedLock myDistributedLock = new MyDistributedLock();
                    myDistributedLock.lock();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, "Thread-" + i).start();
        }
        countDownLatch.countDown();
        System.in.read();
    }
}
