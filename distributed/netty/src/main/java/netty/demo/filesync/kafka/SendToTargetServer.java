package netty.demo.filesync.kafka;

import io.netty.util.internal.ThrowableUtil;
import netty.demo.filesync.audit.Audit;
import netty.demo.filesync.audit.FailedCaused;
import netty.demo.filesync.audit.IAuditService;
import netty.demo.filesync.file.IFile;
import netty.demo.filesync.file.IFileSystem;
import netty.demo.filesync.scanner.FileInfo;
import netty.demo.filesync.task.Task;
import netty.demo.filesync.utils.Utils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class SendToTargetServer implements Runnable {
    private Logger log = LoggerFactory.getLogger(getClass());
    private Task task;
    private boolean endTransfer;
    private FileInfo fileInfo;
    private FileObject fileObject;
    private BlockingQueue<ConsumerRecord<FileInfo, byte[]>> segmentQ;
    private long sessionId;
    private IKafkaService kafkaService;
    private boolean abandonLog;
    private IAuditService auditService;
    private PullData pullData;
    private boolean timeout;

    public SendToTargetServer(Task task, FileInfo fileInfo) {
        this.task = task;
        this.fileInfo = fileInfo;
        this.sessionId = fileInfo.getSessionId();
        this.segmentQ = fileInfo.isCache() ? new LinkedBlockingDeque<>(1) :
                new LinkedBlockingDeque<>(Utils.WAIT_WRITE_SIZE);
    }

    public void setKafkaService(IKafkaService kafkaService) {
        this.kafkaService = kafkaService;
    }

    public void setAuditService(IAuditService auditService) {
        this.auditService = auditService;
    }

    public void setPullData(PullData pullData) {
        this.pullData = pullData;
    }

    protected class FileObject {
        private IFile file;
        private OutputStream out;

        public FileObject(IFile file, OutputStream out) {
            this.file = file;
            this.out = out;
        }

        public IFile getFile() {
            return file;
        }

        public OutputStream getOut() {
            return out;
        }
    }

    @Override
    public void run() {
        send();
    }

    private void send() {
        try {
            while (task.isEnabled()) {
                IFileSystem fs = task.getFileSystem();
                ConsumerRecord<FileInfo, byte[]> consumerRecord = segmentQ.poll(1, TimeUnit.SECONDS);
                if (null == consumerRecord) {
                    log.warn("the after file segment [{}].[{}] has timeout with 1 minute.", fileInfo.getFilePath(), fileInfo.getSegmentId());
                    timeout = true;
                    break;
                }
                long offset = consumerRecord.offset();
                int partition = consumerRecord.partition();
                FileInfo fileInfo = consumerRecord.key();
                byte[] bytes = consumerRecord.value();
                if (log.isDebugEnabled()) {
                    log.debug("receive message offset: [{}], partition:[{}], key:[{}], value length: [{}]", offset, partition, fileInfo.getFilePath(), bytes.length);
                }
                FailedCaused caused = null;
                try {
                    if ((task.isBigFileLocal() && fileInfo.getSegmentId() == 1) || fileInfo.isCache()) {
                        fileObject = getOutputStream(fs, fileInfo);
                    } else {
                        if (this.fileInfo.getSegmentId() >= fileInfo.getSegmentId()) {
                            log.info("consumer message repeated and ignore, file segment: [{}].[{}], last: [{}], current: [{}]", fileInfo.getFilePath(), fileInfo.getSegmentId(), this.fileInfo.getSegmentId(), fileInfo.getSegmentId());
                            continue;
                        }
                        if (this.fileInfo.getSegmentId() + 1 != fileInfo.getSegmentId()) {
                            log.warn("The [{}] file segment is discontinuously, last segmentId: [{}], now segmentId: [{}]",
                                    fileInfo.getFilePath(), this.fileInfo.getSegmentId(), fileInfo.getSegmentId());
                            break;
                        }
                    }
                    if (fileInfo.isCanceled()) {
                        log.info("the file [{}] has canceled transfer, and remove tmp file.", fileInfo.getFilePath());
                        break;
                    }
                    if (fileInfo.isCaughtException()) {
                        log.info("the file [{}].[{}] caught exception, message is [{}].", fileInfo.getFilePath(), fileInfo.getSegmentId(), fileInfo.getExcpetionMsg());
                        break;
                    }
                    OutputStream out = fileObject.getOut();
                    out.write(bytes);
                    if (task.isBigFileLocal() && !fileInfo.isEndSegment()) {
                        continue;
                    }
                    try{
                        out.flush();
                    } catch (NullPointerException npe) {
                        caused = FailedCaused.DIR_CANNOT_WRITE;
                        throw npe;
                    }
                    endTransfer = true;
                    log.info("[{}] from kafka mq has transfered finished, sessionId: {}, task: {}", getFileName(fileInfo.getTmpFileUrl()), fileInfo.getSessionId(), task.getName());
                    recordAudit(fileInfo, Audit.AuditResult.SUCCESS, null);
                    if (endTransfer) {
                        break;
                    }
                } catch (Exception e) {
                    recordAudit(fileInfo, Audit.AuditResult.FAILED, caused);
                    this.fileInfo.setCaughtException(true);
                    log.error("task [{}] file [{}] transfer from kafka to file server error: {}", task.getName(), getFileName(fileInfo.getTmpFileUrl()), ThrowableUtil.stackTraceToString(e));
                    break;
                } finally {
                    if (task.isBigFileLocal() && !fileInfo.isEndSegment() && !fileInfo.isCanceled() && fileInfo.isCaughtException()) {
                        this.fileInfo = fileInfo;
                    }
                }
            }
        } catch (Exception e) {
            log.error("file [{}] error: {}", null == fileInfo ? fileInfo.getFilePath() : "fileInfo is null" , ThrowableUtil.stackTraceToString(e));
        } finally {
            segmentQ.clear();
            pullData.removeSendToServer(fileInfo.getSessionId());
            if (fileObject == null){
                asFileObjectNull(fileInfo);
            }
            endSegment(fileInfo);
            modFileOwner(fileInfo);
        }
    }

    protected void recordAudit(FileInfo fileInfo, Audit.AuditResult result, FailedCaused caused) {
        fileInfo.setTask(task);
        fileInfo.setEndTime(System.currentTimeMillis());
        Audit audit = caused == null ? new Audit(fileInfo, result) :
                new Audit(fileInfo, result, caused);
        auditService.addAudit(audit);
    }

    public void addMessage(ConsumerRecord<FileInfo, byte[]> consumerRecord) {
        try {
            segmentQ.put(consumerRecord);
        } catch (Exception e) {
            log.error("error: {}", ThrowableUtil.stackTraceToString(e));
        }
    }
}
