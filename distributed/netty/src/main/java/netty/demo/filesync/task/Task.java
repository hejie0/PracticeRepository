package netty.demo.filesync.task;

import netty.demo.filesync.file.IFileSystem;

import java.io.Serializable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Task implements Serializable {

    protected int id;
    protected String name;
    protected String ip;
    protected int port;
    protected String username;
    protected String password;
    protected TaskType type;
    protected SyncMode syncMode;
    protected String shareDir;
    protected boolean delEmptyFolder;
    protected int scanInterval;
    protected volatile boolean enabled;
    protected AtomicInteger sendingFileCount = new AtomicInteger(0);
    protected IFileSystem fileSystem;
    protected ScheduledFuture<?> future;
    protected volatile boolean bigFilePullWorking = true;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public SyncMode getSyncMode() {
        return syncMode;
    }

    public void setSyncMode(SyncMode syncMode) {
        this.syncMode = syncMode;
    }

    public String getShareDir() {
        return shareDir;
    }

    public void setShareDir(String shareDir) {
        this.shareDir = shareDir;
    }

    public boolean isDelEmptyFolder() {
        return delEmptyFolder;
    }

    public void setDelEmptyFolder(boolean delEmptyFolder) {
        this.delEmptyFolder = delEmptyFolder;
    }

    public int getScanInterval() {
        return scanInterval;
    }

    public void setScanInterval(int scanInterval) {
        this.scanInterval = scanInterval;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getSendingCount() {
        return sendingFileCount.get();
    }

    public void setSendingFileCount(AtomicInteger sendingFileCount) {
        this.sendingFileCount = sendingFileCount;
    }

    public IFileSystem getFileSystem() {
        return fileSystem;
    }

    public void setFileSystem(IFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public ScheduledFuture<?> getFuture() {
        return future;
    }

    public void setFuture(ScheduledFuture<?> future) {
        this.future = future;
    }

    public boolean isBigFilePullWorking() {
        return bigFilePullWorking;
    }

    public void setBigFilePullWorking(boolean bigFilePullWorking) {
        this.bigFilePullWorking = bigFilePullWorking;
    }

    public abstract void start() throws Exception;

    public abstract void stop() throws Exception;

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
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
                ", fileSystem=" + fileSystem +
                ", future=" + future +
                ", bigFilePullWorking=" + bigFilePullWorking +
                '}';
    }
}
