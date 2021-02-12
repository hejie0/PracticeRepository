package netty.demo.filesync.file;

public class FileAttributes {

    private String name;
    // the file's last modify time, unit is millisecond.
    private long modifyTime;
    private boolean isDirectory;
    private boolean isRegularFile;
    private long size;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public boolean isRegularFile() {
        return isRegularFile;
    }

    public void setRegularFile(boolean regularFile) {
        isRegularFile = regularFile;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "FileAttributes{" +
                "name='" + name + '\'' +
                ", modifyTime=" + modifyTime +
                ", isDirectory=" + isDirectory +
                ", isRegularFile=" + isRegularFile +
                ", size=" + size +
                '}';
    }
}
