package netty.demo.filesync.task;

import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import netty.demo.filesync.scanner.IScanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

@Singleton
public class TaskServiceImpl implements ITaskService {

    private Logger log = LoggerFactory.getLogger(getClass());

    private ReentrantLock lock = new ReentrantLock();
    private Map<String, Task> taskMap = new ConcurrentHashMap();
    @Inject
    private IScanService scanService;

    @Override
    public void init() throws Exception {

    }

    @Override
    public void addTask(Task task) throws Exception {
        try {
            lock.lock();
            Task oldTask = taskMap.get(task.getName());
            if (null != oldTask) {
                log.warn("task is exist, then delete this task and re-create this task, this task name: [{}]", oldTask.getName());
                if (oldTask.isEnabled()) {
                    stop(oldTask.getName());
                    log.info("task stop successfully, this task name: [{}]", oldTask.getName());
                }
                deleteTask(task);
                log.info("task delete successfully, this task name: [{}]", oldTask.getName());
            }

            task.setEnabled(true);

            ExecutorService executorService = null;
            try {
                executorService = Executors.newSingleThreadExecutor();
                Future<String> future = executorService.submit(() -> {
                    task.start();
                    return "success";
                });

                String result = future.get(50, TimeUnit.SECONDS);
                log.info("add to start task result:{}", result);
            } catch (Exception e) {
                task.setEnabled(false);
                throw e;
            } finally {
                if (null != executorService) executorService.shutdown();
            }

            scanService.scan(task);
            taskMap.put(task.getName(), task);
            log.info("scanner start successfully, this task name: [{}]", task.getName());
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void stop(String taskName) throws Exception {
        try {
            lock.lock();
            Task task = taskMap.get(taskName);
            if (null == task) {
                log.warn("getting task from task map is null, task name: {}", taskName);
                throw new Exception("task is non-exist");
            }

            task.setEnabled(false);

            ExecutorService executorService = null;
            try {
                scanService.disScan(task);
                executorService = Executors.newSingleThreadExecutor();
                Future<String> future = executorService.submit(() -> {
                    task.stop();
                    return "success";
                });

                String result = future.get(50, TimeUnit.SECONDS);
                log.info("stop or delete result:{}", result);
            } catch (Exception e) {
                log.info("this task [task name: {}] stop failed! error{}", taskName, Throwables.getStackTraceAsString(e));
                task.setEnabled(true);
                scanService.scan(task);
                throw e;
            } finally {
                if (null != executorService) executorService.shutdown();
            }

            taskMap.remove(taskName);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void deleteTask(Task task) {
    }
}
