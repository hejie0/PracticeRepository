package netty.demo.filesync.kafka;

import io.netty.util.internal.ThrowableUtil;
import netty.demo.filesync.scanner.FileInfo;
import netty.demo.filesync.task.Task;
import netty.demo.filesync.utils.Utils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.KafkaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @Author: hejie
 * @Date: 2021/5/31 13:28
 * @Version: 1.0
 */
public class StoreDataToKafka implements Runnable {

    private Logger log = LoggerFactory.getLogger(getClass());
    private Task task;
    private BatchSize batchSize;
    private ProducerRecord<FileInfo, byte[]> record;
    private BlockingQueue<ProducerRecord<FileInfo, byte[]>> recordQ;
    private Future future;

    public StoreDataToKafka(Task task, BatchSize batchSize) {
        switch (batchSize) {
            case HUNDRED_OF_K:
                recordQ = new LinkedBlockingDeque<>(100);
                break;
            case TENS_OF_K:
                recordQ = new LinkedBlockingDeque<>(2000);
                break;
            case BELOW_TEN_OF_K:
                recordQ = new LinkedBlockingDeque<>(10000);
                break;
            default:
                log.error("task [{}] unknown batchSize, this batchSize:[{}]", task.getName(), batchSize);
                break;
        }
        this.task = task;
        this.batchSize = batchSize;
    }

    public void addMessage(ProducerRecord<FileInfo, byte[]> record) {
        FileInfo fileInfo = record.key();
        try {
            recordQ.put(record);
        } catch (Throwable t) {
            log.error("file segment: [{}].[{}], error: {}", fileInfo.getFilePath(), fileInfo.getSegmentId(), ThrowableUtil.stackTraceToString(t));
        }
    }

    @Override
    public void run() {
        Producer<FileInfo, byte[]> producer = null;
        try {
            producer = getProducer(batchSize);
            while (true) {
                try {
                    if (!task.isEnabled()) {
                        log.warn("task is disabled, StoreDataToKafka will exit, task id: {}", task.getId());
                        break;
                    }
                    record = recordQ.take();
                    Callback callback = new ToKafkaResult(record);
                    try {
                        producer.send(record, callback);
                    } catch (KafkaException e) {
                        log.error("producer.send error: {}", ExceptionUtils.getStackTrace(e));
                    }
                } catch (InterruptedException ie) {
                    log.info("task: [{}], batch size: [{}] will disable, and then interrupte this condition normally", task.getName(), this.batchSize);
                } catch (Exception e) {
                    log.error("task: [{}], batch size: [{}] error: {}", task.getName(), this.batchSize, ThrowableUtil.stackTraceToString(e));
                }
            }
        } catch (Throwable t) {
            log.error("error: {}", ExceptionUtils.getStackTrace(t));
        } finally {
            recordQ.clear();
            if (producer != null) producer.close();
            if (task.isEnabled()) {
                log.error("thread {} exit, task: {}", Thread.currentThread().getName(), task.getName());
            } else {
                log.info("thread {} describe {} exit normally after task [{}] disabled.", Thread.currentThread().getName(), this.batchSize, task.getName());
            }
        }
    }

    private Producer<FileInfo, byte[]> getProducer(BatchSize batchSize) {
        long batch_size = getBatchSize(batchSize);
        long buffer_memory = getBufferMemory(batchSize);
        long linger = getLinger(batchSize);

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "task-" + task.getId());
        props.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, Utils.PULL_REQUEST_SIZE);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "netty.demo.filesync.kafka.FileInfoSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArraySerializer");
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, buffer_memory);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, batch_size);
        props.put(ProducerConfig.LINGER_MS_CONFIG, linger);
        props.put(ProducerConfig.ACKS_CONFIG, 1);
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 50);
        return new KafkaProducer<>(props);
    }

    private long getLinger(BatchSize batchSize) {
        switch (batchSize) {
            case HUNDRED_OF_K:
                return 0;
            default:
                return 20;
        }
    }

    private long getBufferMemory(BatchSize batchSize) {
        switch (batchSize) {
            case BELOW_TEN_OF_K:
                return 32 * 1024 * 1024;
            case TENS_OF_K:
                return 16 * 1024 * 1024;
            case HUNDRED_OF_K:
                return 2 * 1024 * 1024;
            default:
                log.error("task [{}] unknown batchSize, this batchSize:[{}]", task.getName(), batchSize);
                return 0;
        }
    }

    private long getBatchSize(BatchSize batchSize) {
        switch (batchSize) {
            case HUNDRED_OF_K:
                return Utils.FILE_SEGMENT_SIZE;
            case TENS_OF_K:
                return 50 * 1024;
            case BELOW_TEN_OF_K:
                return 10 * 1024;
            default:
                log.error("task [{}] unknown batchSize, this batchSize:[{}]", task.getName(), batchSize);
                return 0;
        }
    }

    public void setFuture(Future future) {
        this.future = future;
    }

    public Future getFuture() {
        return future;
    }
}
