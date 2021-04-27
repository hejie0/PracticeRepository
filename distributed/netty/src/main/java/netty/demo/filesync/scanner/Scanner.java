package netty.demo.filesync.scanner;

import io.netty.util.internal.ThrowableUtil;
import netty.demo.filesync.file.FileAttributes;
import netty.demo.filesync.file.IDirectory;
import netty.demo.filesync.file.IFileSystem;
import netty.demo.filesync.sender.ISendService;
import netty.demo.filesync.task.SyncMode;
import netty.demo.filesync.task.Task;
import netty.demo.filesync.task.TaskStoppedException;
import netty.demo.filesync.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Scanner implements Runnable {

    private Logger log = LoggerFactory.getLogger(getClass());

    private Task task;
    private IFileSystem fileSystem;
    private ISendService sendService;
    private boolean firstOut = true;
    private long logTmFlag = System.currentTimeMillis();
    private Map<String, String> copyModeFileNames = new HashMap<>();

    public Task getTask() {
        return task;
    }

    public Scanner setTask(Task task) {
        this.task = task;
        this.fileSystem = task.getFileSystem();
        return this;
    }

    public ISendService getSendService() {
        return sendService;
    }

    public Scanner setSendService(ISendService sendService) {
        this.sendService = sendService;
        return this;
    }

    @Override
    public void run() {
        try {
            if (task.getSendingCount() != 0) {
                long now = System.currentTimeMillis();
                if (firstOut || (now - logTmFlag > 60 * 1000)) {
                    log.info("there are files in sending queue for task: {}, sending count: {}",
                            task.getName(), task.getSendingCount());
                    firstOut = false;
                    logTmFlag = now;
                }
                return;
            }

            scanDir("/");
            //delete empty folder when sync modle is moving
            if (SyncMode.SYNC_MODE_MOVE.equals(task.getSyncMode()) && task.isDelEmptyFolder()) {
                FileUtils.removeEmptyFolder(task, "/");
            }
        } catch (TaskStoppedException e) {
            log.info("task is stopped, task: {}, stop the scanner", task.getName());
            task.getFuture().cancel(false);
        } catch (Exception e) {
            log.error("scan dir failed, task: {}, error: {}", task.getName(), ThrowableUtil.stackTraceToString(e));
        }
    }

    private void scanDir(String dir) throws Exception {
        if (!task.isEnabled()) {
            return;
        }

        IDirectory directory = null;
        try {
            directory = fileSystem.openDir(task, dir);
            while (true) {
                FileAttributes attributes = directory.readNextFile();
                if (!task.isEnabled() || null == attributes) {
                    break;
                } else if (".".equals(attributes.getName()) || "..".equals(attributes.getName())) {
                    continue;
                }

                String filePath = dir + attributes.getName();
                if (!copyModeFileNames.containsKey(filePath)){
                    continue;
                }

                FileInfo fileInfo = new FileInfo();
                if (attributes.isDirectory()) {
                    fileInfo.setFolder(true);
                    fileInfo.setFilePath(filePath);
                    fileInfo.setTask(task);
                    fileInfo.setModifyTime(0);
                    fileInfo.setBeginTime(System.currentTimeMillis());
                    sendService.send(fileInfo);
                    // walk child dir.
                    scanDir(filePath + "/");
                } else {

                }
            }
        } catch (Exception e) {

        }
    }
}
