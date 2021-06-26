package netty.demo.filesync.audit;

public enum FailedCaused {

    UNKNOWN("未知"),
    DIR_CANNOT_WRITE("共享目录无写入权限");

    private String caused;

    FailedCaused(String caused) {
        this.caused = caused;
    }

    public String getCaused() {
        return caused;
    }
}
