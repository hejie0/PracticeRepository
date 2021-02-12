package netty.demo.filesync.task;

import netty.demo.filesync.file.FtpFileSystem;

public class FtpTask extends Task {

    private String ftpCharSet;
    private FtpWorkPattern ftpWorkPattern;

    public String getFtpCharSet() {
        return ftpCharSet;
    }

    public void setFtpCharSet(String ftpCharSet) {
        this.ftpCharSet = ftpCharSet;
    }

    public FtpWorkPattern getFtpWorkPattern() {
        return ftpWorkPattern;
    }

    public void setFtpWorkPattern(FtpWorkPattern ftpWorkPattern) {
        this.ftpWorkPattern = ftpWorkPattern;
    }

    @Override
    public void start() throws Exception {
        FtpFileSystem fileSystem = new FtpFileSystem(shareDir);
        fileSystem.setFtpCharset(ftpCharSet);
        fileSystem.setFtpWorkPattern(ftpWorkPattern);
        fileSystem.setIp(ip);
        fileSystem.setPort(port);
        fileSystem.setUsername(username);
        fileSystem.setPassword(password);
        this.fileSystem = fileSystem;
    }

    @Override
    public void stop() throws Exception {
        fileSystem.close();
    }

    @Override
    public String toString() {
        return "FtpTask{" +
                "ftpCharSet='" + ftpCharSet + '\'' +
                ", ftpWorkPattern=" + ftpWorkPattern +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", type=" + type +
                ", syncMode=" + syncMode +
                ", shareDir='" + shareDir + '\'' +
                ", delEmptyFolder=" + delEmptyFolder +
                ", scanInterval=" + scanInterval +
                ", enabled=" + enabled +
                ", sendingFileCount=" + sendingFileCount +
                '}';
    }
}
