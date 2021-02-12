package netty.demo.filesync.scanner;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import netty.demo.filesync.sender.ISendService;
import netty.demo.filesync.task.Task;
import netty.demo.filesync.threadpool.IThreadPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Singleton
public class ScanServiceImpl implements IScanService {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    private ISendService sendService;
    @Inject
    private IThreadPoolService threadPoolService;

    @Override
    public void scan(Task task) {
        Scanner scanner = new Scanner();
        scanner.setTask(task)
                .setSendService(sendService);
        ScheduledFuture<?> future = threadPoolService.getScheduledService().scheduleWithFixedDelay(
                scanner, 3000, task.getScanInterval(), TimeUnit.MILLISECONDS);
        task.setFuture(future);
        log.info("start scanner for task: {}", task.getName());
    }

    @Override
    public void disScan(Task task) {
        task.getFuture().cancel(true);
        log.info("scanner is stoped, task: {}", task);
    }
}
