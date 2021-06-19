package netty.demo.filesync.kafka;

import io.netty.util.internal.ThrowableUtil;
import netty.demo.filesync.scanner.FileInfo;
import netty.demo.filesync.task.Task;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @Author: hejie
 * @Date: 2021/5/31 17:24
 * @Version: 1.0
 */
public class ToKafkaGroup {
    private Logger log = LoggerFactory.getLogger(getClass());
    private Map<BatchSize, StoreDataToKafka> toKafkaMap = new ConcurrentHashMap<>();

    protected ToKafkaGroup(Task task, ExecutorService storeExecutor) {
        for (BatchSize batchSize : BatchSize.values()) {
            StoreDataToKafka toKafka = new StoreDataToKafka(task, batchSize);
            Future future = storeExecutor.submit(toKafka);
            toKafka.setFuture(future);
            toKafkaMap.put(batchSize, toKafka);
        }
    }

    public void addMessage(ProducerRecord<FileInfo, byte[]> record) {
        try {
            FileInfo fileInfo = record.key();
            BatchSize batchSize = getBatchSize(fileInfo);
            StoreDataToKafka toKafka = toKafkaMap.get(batchSize);
            if (toKafka == null) {
                log.warn("can not find StoreDataToKafka Object of toKafkaMap, sessionId: [{}], [{}].[{}]", fileInfo.getSessionId(), fileInfo.getFilePath(), fileInfo.getSegmentId());
                return;
            }
            toKafka.addMessage(record);
        } catch (Exception e) {
            log.error("error: {}", ThrowableUtil.stackTraceToString(e));
        }
    }

    public BatchSize getBatchSize(FileInfo fileInfo) {
        long size = fileInfo.getSize();
        if (size < 10 * 1024) {
            return BatchSize.BELOW_TEN_OF_K;
        } else if (size >= 10 * 1024 && size < 100 * 1024) {
            return BatchSize.TENS_OF_K;
        } else {
            return BatchSize.HUNDRED_OF_K;
        }
    }

    public void remove(BatchSize batchSize) {
        toKafkaMap.remove(batchSize);
    }

    public void clear() {
        try {
            for (BatchSize batchSize : BatchSize.values()) {
                toKafkaMap.get(batchSize).getFuture().cancel(true);
            }
            toKafkaMap.clear();
        } catch (Exception e) {
            log.error("error: {}", ThrowableUtil.stackTraceToString(e));
        }
    }


}
