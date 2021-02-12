package netty.demo.filesync.threadpool;

import com.google.inject.Singleton;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class ThreadPoolServiceImpl implements IThreadPoolService {

    private ScheduledExecutorService scheduledExecutorService;

    public ScheduledExecutorService getScheduledExecutorService() {
        final ThreadGroup tg = new ThreadGroup("Scheduled Task Threads");
        ThreadFactory f = new ThreadFactory() {
            AtomicInteger id = new AtomicInteger();

            @Override
            public Thread newThread(Runnable runnable) {
                return new Thread(tg, runnable,
                        "FILESYNC-SCHEDULED-" + id.getAndIncrement());
            }
        };
        return Executors.newScheduledThreadPool(22, f);
    }

    @Override
    public ScheduledExecutorService getScheduledService() {
        if (scheduledExecutorService == null) {
            scheduledExecutorService = getScheduledExecutorService();
        }
        return scheduledExecutorService;
    }

    @Override
    public ExecutorService createExcutor(String threadGroupName, String threadNamePrefix, int coreSize, int maxSize) {
        final ThreadGroup tg = new ThreadGroup(threadGroupName);
        ThreadFactory f = new ThreadFactory() {
            AtomicInteger id = new AtomicInteger();

            @Override
            public Thread newThread(Runnable runnable) {
                return new Thread(tg, runnable,
                        threadNamePrefix + id.getAndIncrement());
            }
        };
        return new ThreadPoolExecutor(coreSize, maxSize,60L,
                TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), f);
    }


}
