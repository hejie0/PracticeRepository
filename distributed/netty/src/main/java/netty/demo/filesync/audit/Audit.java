package netty.demo.filesync.audit;

import netty.demo.filesync.scanner.FileInfo;

/**
 * @Author: hejie
 * @Date: 2021/6/3 11:07
 * @Version: 1.0
 */
public class Audit {

    private FileInfo fileInfo;
    private AuditResult result;
    private String failedCaused;

    public enum AuditResult {
        SUCCESS, FAILED
    }

    public Audit(FileInfo fileInfo, AuditResult result) {
        this.fileInfo = fileInfo;
        this.result = result;
    }

    public Audit(FileInfo fileInfo, AuditResult result, FailedCaused failedCaused) {
        this.fileInfo = fileInfo;
        this.result = result;
        this.failedCaused = failedCaused.getCaused();
    }

    public FileInfo getFileInfo() {
        return fileInfo;
    }

    public void setFileInfo(FileInfo fileInfo) {
        this.fileInfo = fileInfo;
    }

    public AuditResult getResult() {
        return result;
    }

    public void setResult(AuditResult result) {
        this.result = result;
    }

    public String getFailedCaused() {
        return failedCaused;
    }

    public void setFailedCaused(String failedCaused) {
        this.failedCaused = failedCaused;
    }
}
