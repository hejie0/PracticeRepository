package netty.demo.filesync.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface IFile {

    void create() throws IOException;

    InputStream getInputStream() throws IOException;

    InputStream getInputStream(String path) throws IOException;

    OutputStream getOutputSteam() throws IOException;

    OutputStream getOutputSteam(String path) throws IOException;

    boolean renameTo(String path) throws IOException;

    boolean delete() throws IOException;

    void close();

}
