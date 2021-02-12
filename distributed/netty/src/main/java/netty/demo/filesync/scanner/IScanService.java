package netty.demo.filesync.scanner;

import com.google.inject.ImplementedBy;
import netty.demo.filesync.task.Task;

@ImplementedBy(ScanServiceImpl.class)
public interface IScanService {

    void scan(Task task);

    void disScan(Task task);
}
