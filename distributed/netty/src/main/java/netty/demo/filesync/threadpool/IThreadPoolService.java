package netty.demo.filesync.threadpool;

import com.google.inject.ImplementedBy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

@ImplementedBy(ThreadPoolServiceImpl.class)
public interface IThreadPoolService {

    ScheduledExecutorService getScheduledService();

    ExecutorService createExcutor(String threadGroupName, String threadNamePrefix, int coreSize, int maxSize);
}
