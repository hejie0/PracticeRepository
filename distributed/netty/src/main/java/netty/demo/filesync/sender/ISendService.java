package netty.demo.filesync.sender;

import com.google.inject.ImplementedBy;
import netty.demo.filesync.scanner.FileInfo;

@ImplementedBy(SendServiceImpl.class)
public interface ISendService {

    void send(FileInfo fileInfo);

    void setRateLimit(double permitsPerSecond);

}
