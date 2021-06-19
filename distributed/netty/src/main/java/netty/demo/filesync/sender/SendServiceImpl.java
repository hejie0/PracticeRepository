package netty.demo.filesync.sender;

import com.google.inject.Singleton;
import netty.demo.filesync.scanner.FileInfo;

@Singleton
public class SendServiceImpl implements ISendService {

    @Override
    public void init() throws Exception {

    }

    @Override
    public void send(FileInfo fileInfo) {

    }

    @Override
    public void setRateLimit(double permitsPerSecond) {

    }
}
