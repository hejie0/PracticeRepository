package netty.demo.filesync.kafka;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.netty.util.internal.ThrowableUtil;
import netty.demo.filesync.audit.IAuditService;
import netty.demo.filesync.scanner.FileInfo;
import netty.demo.filesync.task.Task;
import netty.demo.filesync.threadpool.IThreadPoolService;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @Author: hejie
 * @Date: 2021/6/1 9:49
 * @Version: 1.0
 */
@Singleton
public class KafkaServiceImpl implements IKafkaService {

    private Logger log = LoggerFactory.getLogger(getClass());
    private final int CORE_SIZE = 8;
    private final int MAX_SIZE = 128;
    private ExecutorService storeExecutor;
    private ExecutorService pullExecutor;
    private Map<Integer, ToKafkaGroup> toKafkaGroupMap = new ConcurrentHashMap<>();
    private Map<Integer, ExecutorService> sendExecutorMap = new ConcurrentHashMap<>();

    @Inject
    private IThreadPoolService threadPoolService;

    @Inject
    private IAuditService auditService;

    @Override
    public void init() throws Exception {
        log.info("init kafka service");
        storeExecutor = threadPoolService.createExcutor("TO-KAFKA", "to-kafka-", 4, 100);
        pullExecutor = threadPoolService.createExcutor("FROM-KAFKA", "from-kafka-", 1, 50);
        setRejected(storeExecutor);
        setRejected(pullExecutor);
        log.info("kafka service is inited!");
    }

    @Override
    public void startSendToKafka(Task task) {
        ExecutorService sendExecutor = threadPoolService.createExcutor("SEND-TO-KAFKA",
                String.format("to-topic-[%s]", task.getId()), CORE_SIZE, MAX_SIZE);
        setRejected(sendExecutor);
        sendExecutorMap.put(task.getId(), sendExecutor);

        ToKafkaGroup toKafkaGroup = new ToKafkaGroup(task, storeExecutor);
        toKafkaGroupMap.put(task.getId(), toKafkaGroup);

        PullData pullData = new PullData(task);
        pullData.setKafkaService(this);
        pullData.setAuditService(auditService);
        pullExecutor.submit(pullData);
    }

    @Override
    public void stopSendToKafka(Task task) {
        toKafkaGroupMap.get(task.getId()).clear();
        toKafkaGroupMap.remove(task.getId());
    }

    @Override
    public void sendToKafka(FileInfo fileInfo, byte[] fileByte) {
        String topic = String.valueOf(fileInfo.getTask().getId());
        int partition = getTaskPartition(fileInfo);
        ProducerRecord<FileInfo, byte[]> record = new ProducerRecord<>(topic, partition, fileInfo, fileByte);
        ToKafkaGroup toKafkaGroup = null;
        try {
            toKafkaGroup = toKafkaGroupMap.get(fileInfo.getTask().getId());
            if (null == toKafkaGroup) {
                log.warn("can not find ToKafkaGroup Object of toKafkaGroupMap, sessionId: [{}], [{}].[{}]", fileInfo.getSessionId(), fileInfo.getFilePath(), fileInfo.getSegmentId());
                return;
            }
            toKafkaGroup.addMessage(record);
        } catch (Exception e) {
            log.error("error: {}", ThrowableUtil.stackTraceToString(e));
        }
    }

    @Override
    public void close() {

    }

    @Override
    public ExecutorService getSendExecutor(int taskId) {
        return sendExecutorMap.get(taskId);
    }

    private int getTaskPartition(FileInfo fileInfo) {
        if (fileInfo.isCache()) {
            return 1;
        }
        return 0;
    }

    private void setRejected(ExecutorService sendExecutor) {
        RejectedExecutionHandler handler = (runnable, executor) -> {
            if (executor.isShutdown()) {
                try {
                    executor.getQueue().put(runnable);
                } catch (InterruptedException e) {
                    log.error("error: {}", ThrowableUtil.stackTraceToString(e));
                    Thread.currentThread().interrupt();
                }
            }
        };
        ThreadPoolExecutor poolExecutor = (ThreadPoolExecutor) sendExecutor;
        poolExecutor.setRejectedExecutionHandler(handler);
    }

}
