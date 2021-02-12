package netty.demo.filesync.file;

import io.netty.util.internal.ThrowableUtil;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;

public class FtpFile implements IFile {

    private Logger log = LoggerFactory.getLogger(getClass());

    private String filePath;
    private FTPClient ftpClient;
    private FtpFileSystem ftpFileSystem;
    private boolean isOpened = true;

    public FtpFile(String filePath, FTPClient ftpClient, FtpFileSystem ftpFileSystem) {
        this.filePath = filePath;
        this.ftpClient = ftpClient;
        this.ftpFileSystem = ftpFileSystem;
    }

    private static class FtpInputStream extends InputStream {

        private Logger log = LoggerFactory.getLogger(getClass());

        InputStream in;
        FTPClient ftpClient;

        public FtpInputStream(InputStream in, FTPClient ftpClient) {
            this.in = in;
            this.ftpClient = ftpClient;
        }

        @Override
        public int read() throws IOException {
            return in.read();
        }

        @Override
        public int read(byte[] b) throws IOException {
            return in.read(b);
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return in.read(b, off, len);
        }

        @Override
        public void close() throws IOException {
            in.close();
            ftpClient.completePendingCommand();
        }
    }

    private static class FtpOutputStream extends OutputStream {

        private Logger log = LoggerFactory.getLogger(getClass());

        OutputStream out;
        FTPClient ftpClient;

        public FtpOutputStream(OutputStream out, FTPClient ftpClient) {
            this.out = out;
            this.ftpClient = ftpClient;
        }

        @Override
        public void write(byte[] b) throws IOException {
            out.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            out.write(b, off, len);
        }

        @Override
        public void flush() throws IOException {
            out.flush();
        }

        @Override
        public void close() throws IOException {
            out.close();
            ftpClient.completePendingCommand();
        }

        @Override
        public void write(int b) throws IOException {
            out.write(b);
        }
    }

    @Override
    public void create() throws IOException {

    }

    @Override
    public InputStream getInputStream() throws IOException {
        String absPath = ftpFileSystem.buildAbsolutePath(filePath);
        return getInputStream(absPath);
    }

    @Override
    public InputStream getInputStream(String path) throws IOException {
        InputStream in = null;
        try {
            in = ftpClient.retrieveFileStream(path);
            if (in != null) {
                return new FtpFile.FtpInputStream(in, ftpClient);
            }
        } catch (ConnectException e) {
            log.error("error: {}", ThrowableUtil.stackTraceToString(e));
        } finally {
            if (null == in) {
                ftpFileSystem.returnClient(ftpClient);
            }
        }
        return null;
    }

    @Override
    public OutputStream getOutputSteam() throws IOException {
        String absPath = ftpFileSystem.buildAbsolutePath(filePath);
        return getOutputSteam(absPath);
    }

    @Override
    public OutputStream getOutputSteam(String path) throws IOException {
        OutputStream out = null;
        try {
            out = ftpClient.storeFileStream(path);
            if (null != out) {
                return new FtpFile.FtpOutputStream(out, ftpClient);
            }
        } catch (ConnectException e) {
            log.error("error: {}", ThrowableUtil.stackTraceToString(e));
        } finally {
            if (null == out) {
                ftpFileSystem.returnClient(ftpClient);
            }
        }
        return null;
    }

    @Override
    public boolean renameTo(String path) throws IOException {
        String from = ftpFileSystem.buildAbsolutePath(filePath);
        String to = ftpFileSystem.buildAbsolutePath(path);
        return ftpClient.rename(from, to);
    }

    @Override
    public boolean delete() throws IOException {
        String absPath = ftpFileSystem.buildAbsolutePath(filePath);
        return ftpClient.deleteFile(absPath);
    }

    @Override
    public void close() {
        if (isOpened && null != ftpClient) {
            ftpFileSystem.returnClient(ftpClient);
            isOpened = false;
        }
    }

}
