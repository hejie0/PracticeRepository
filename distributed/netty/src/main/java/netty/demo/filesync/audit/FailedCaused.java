package netty.demo.filesync.audit;

public enum FailedCaused {

    UNKNOWN("未知");

    private String caused;

    FailedCaused(String caused) {
        this.caused = caused;
    }

    public String getCaused() {
        return caused;
    }
}
