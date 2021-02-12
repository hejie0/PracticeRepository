package netty.demo.filesync.file;

import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;

public class FtpDirectory implements IDirectory {

    private FTPFile[] ftpFiles;
    private int nextIndex = 0;

    public FtpDirectory(FTPFile[] ftpFiles) {
        this.ftpFiles = ftpFiles;
    }

    @Override
    public FileAttributes readNextFile() {
        if (null == ftpFiles || ftpFiles.length == 0) {
            return null;
        }

        FTPFile ftpFile = ftpFiles[nextIndex];
        nextIndex++;
        FileAttributes attributes = new FileAttributes();
        attributes.setSize(ftpFile.getSize());
        attributes.setModifyTime(ftpFile.getTimestamp().getTimeInMillis());
        attributes.setRegularFile(ftpFile.isFile());
        attributes.setDirectory(ftpFile.isDirectory());
        attributes.setName(ftpFile.getName());
        return attributes;
    }

    @Override
    public void close() throws IOException {

    }
}
