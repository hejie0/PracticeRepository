package netty.demo.filesync.task;

import com.google.inject.ImplementedBy;

@ImplementedBy(TaskServiceImpl.class)
public interface ITaskService {

    void addTask(Task task) throws Exception;

    void stop(String taskName) throws Exception;

    void deleteTask(Task task) throws Exception;

}
