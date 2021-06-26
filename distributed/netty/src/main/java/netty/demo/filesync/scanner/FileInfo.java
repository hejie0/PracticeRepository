package netty.demo.filesync.scanner;

import netty.demo.filesync.task.Task;
import netty.demo.filesync.utils.Utils;

public class FileInfo {

    private Task task;
    private String filePath;
    private boolean folder;
    private long modifyTime;
    private long beginTime;
    private long endTime;
    private long size;
    private boolean canceled;
    private boolean caughtException;

    private long segmentId;
    private long sessionId;
    private boolean endSegment;

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isFolder() {
        return folder;
    }

    public void setFolder(boolean folder) {
        this.folder = folder;
    }

    public long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(long segmentId) {
        this.segmentId = segmentId;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public boolean isCache() {
        if (size <= Utils.FILE_SEGMENT_SIZE) {
            return true;
        }
        return false;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public boolean isCaughtException() {
        return caughtException;
    }

    public void setCaughtException(boolean caughtException) {
        this.caughtException = caughtException;
    }

    public boolean isEndSegment() {
        return endSegment;
    }

    public void setEndSegment(boolean endSegment) {
        this.endSegment = endSegment;
    }
}
