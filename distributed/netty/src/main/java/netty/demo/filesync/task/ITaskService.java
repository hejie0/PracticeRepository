package netty.demo.filesync.task;

import com.google.inject.ImplementedBy;
import netty.demo.filesync.core.IService;

@ImplementedBy(TaskServiceImpl.class)
public interface ITaskService extends IService {

    void addTask(Task task) throws Exception;

    void stop(String taskName) throws Exception;

    void deleteTask(Task task) throws Exception;

}
