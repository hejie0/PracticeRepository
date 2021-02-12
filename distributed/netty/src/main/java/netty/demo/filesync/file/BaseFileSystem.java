package netty.demo.filesync.file;

public abstract class BaseFileSystem implements IFileSystem {

    protected String basePath;

    @Override
    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public BaseFileSystem(String basePath) {
        this.basePath = basePath;
    }

    @Override
    public String buildAbsolutePath(String path) {
        return basePath + path;
    }

}
