package netty.demo.filesync.scanner;

import com.google.inject.ImplementedBy;
import netty.demo.filesync.core.IService;
import netty.demo.filesync.task.Task;

@ImplementedBy(ScanServiceImpl.class)
public interface IScanService extends IService {

    void scan(Task task);

    void disScan(Task task);
}
