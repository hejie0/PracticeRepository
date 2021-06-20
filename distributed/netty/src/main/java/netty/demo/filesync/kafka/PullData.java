package netty.demo.filesync.kafka;

import io.netty.util.internal.ThrowableUtil;
import netty.demo.filesync.audit.IAuditService;
import netty.demo.filesync.scanner.FileInfo;
import netty.demo.filesync.task.Task;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * @Author: hejie
 * @Date: 2021/6/3 11:00
 * @Version: 1.0
 */
public class PullData implements Runnable {

    private Logger log = LoggerFactory.getLogger(getClass());

    private Task task;
    private IKafkaService kafkaService;
    private IAuditService auditService;
    private static long pullTimeout = 1000;
    private long startTime = System.currentTimeMillis();
    private Map<Long, SendToTargetServer> sendToServerMap = new ConcurrentHashMap<>();

    public PullData(Task task) {
        this.task = task;
    }

    public void setAuditService(IAuditService auditService) {
        this.auditService = auditService;
    }

    public void setKafkaService(IKafkaService kafkaService) {
        this.kafkaService = kafkaService;
    }

    @Override
    public void run() {
        pull(task);
    }

    private void pull(Task task) {
        Consumer<FileInfo, byte[]> consumer = null;
        try {
            consumer = getConsumer(task);
            consumer.subscribe(Arrays.asList(String.valueOf(task.getId())));
            ConsumerRecords<FileInfo, byte[]> consumerRecords = null;
            ConsumerRecord<FileInfo, byte[]> commitRecord = null;
            ExecutorService sendExecutor = kafkaService.getSendExecutor(task.getId());
            Collection<TopicPartition> partitions = getPartition(task);
            while (task.isEnabled()) {
                try {
                    consumerRecords = consumer.poll(Duration.ofSeconds(pullTimeout));
                    boolean changeFlag = System.currentTimeMillis() - startTime > 2 * 60 * 1000;
                    if (null == consumerRecords || consumerRecords.isEmpty() && changeFlag) {
                        changeConsumerPartition(consumer);
                        continue;
                    }
                    for (ConsumerRecord<FileInfo, byte[]> consumerRecord : consumerRecords) {
                        sendToTargetServer(sendExecutor, consumerRecord);
                        commitRecord = consumerRecord;
                    }
                } catch (Exception e) {
                    log.error("task and partition is {}, error: {}", partitions, ThrowableUtil.stackTraceToString(e));
                } finally {
                    if (null != commitRecord) {
                        commitAsync(consumer, commitRecord);
                        commitRecord = null;
                    }
                }
            }
        } catch (Throwable t) {
            log.error("error: {}", ThrowableUtil.stackTraceToString(t));
        } finally {
            if (null != consumer) {
                consumer.close();
            }
        }
    }

    private void commitAsync(Consumer<FileInfo, byte[]> consumer, ConsumerRecord<FileInfo, byte[]> commitRecord) {
        Map<TopicPartition, OffsetAndMetadata> commitOffsets = new HashMap<>();
        commitOffsets.put(new TopicPartition(commitRecord.topic(), commitRecord.partition()),
                new OffsetAndMetadata(commitRecord.offset() + 1, "no metadata"));

        consumer.commitAsync(commitOffsets, (offsets, e) -> {
            if (null == e) {
                return;
            }
            log.error("async commit failed for offset {}, error: {}", offsets, ThrowableUtil.stackTraceToString(e));
            ;
        });
    }

    private void sendToTargetServer(ExecutorService sendExecutor, ConsumerRecord<FileInfo, byte[]> consumerRecord) {
        SendToTargetServer sendToTargetServer = null;
        FileInfo fileInfo = consumerRecord.key();
        if (sendToServerMap.containsKey(fileInfo.getSessionId())) {
            sendToTargetServer = sendToServerMap.get(fileInfo.getSessionId());
            // if file wrote to target server, but cause exception, then remove the sessionId from sendToServerMap, so the object is null.
            if (null == sendToTargetServer) {
                return;
            }
        } else {
            if (fileInfo.getSegmentId() > 1) {
                return;
            }
            sendToTargetServer = new SendToTargetServer(task, fileInfo);
            sendToTargetServer.setPullData(this);
            sendToTargetServer.setKafkaService(kafkaService);
            sendToTargetServer.setAuditService(auditService);
            sendToServerMap.put(fileInfo.getSessionId(), sendToTargetServer);
            sendExecutor.submit(sendToTargetServer);
        }
        try {
            sendToTargetServer.addMessage(consumerRecord);
        } catch (Exception e) {
            log.error("file segment: [{}].[{}], error: {}", fileInfo.getFilePath(), fileInfo.getSegmentId(), ThrowableUtil.stackTraceToString(e));
        }
    }

    private void changeConsumerPartition(Consumer<FileInfo, byte[]> consumer) {
        TopicPartition topicPartition = null;
        try {
            if (sendToServerMap.size() > 0) {
                return;
            }
            if (task.isBigFilePullWorking()) {
                task.setBigFilePullWorking(false);
                topicPartition = new TopicPartition(String.valueOf(task.getId()), 0);
                consumer.pause(Arrays.asList(topicPartition));
                topicPartition = new TopicPartition(String.valueOf(task.getId()), 1);
                consumer.resume(Arrays.asList(topicPartition));
            } else {
                task.setBigFilePullWorking(true);
                topicPartition = new TopicPartition(String.valueOf(task.getId()), 1);
                consumer.pause(Arrays.asList(topicPartition));
                topicPartition = new TopicPartition(String.valueOf(task.getId()), 0);
                consumer.resume(Arrays.asList(topicPartition));
            }
        } catch (IllegalStateException e) {
            log.error("topicPartition of task {} has not initialized. error: {}",
                    task.getName(), ThrowableUtil.stackTraceToString(e));
            startTime = System.currentTimeMillis();
        }
    }

    private Collection<TopicPartition> getPartition(Task task) {
        int partition = 0;
        if (!task.isBigFilePullWorking()) {
            partition = 1;
        }
        TopicPartition topicPartition = new TopicPartition(String.valueOf(task.getId()), partition);
        Collection<TopicPartition> partitions = Arrays.asList(topicPartition);
        return partitions;
    }

    private Consumer<FileInfo, byte[]> getConsumer(Task task) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "task-" + task.getId());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 8);
        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, 8404992);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 100000);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 61000);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "netty.demo.filesync.kafka.FileInfoDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        return new KafkaConsumer<>(props);
    }
}
