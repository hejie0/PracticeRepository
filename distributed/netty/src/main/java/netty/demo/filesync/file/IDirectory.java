package netty.demo.filesync.file;

import java.io.IOException;

public interface IDirectory {
    FileAttributes readNextFile();
    void close() throws IOException;
}
