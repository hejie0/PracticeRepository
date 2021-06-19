package netty.demo.filesync.kafka;

import io.netty.util.internal.ThrowableUtil;
import netty.demo.filesync.scanner.FileInfo;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: hejie
 * @Date: 2021/5/31 16:49
 * @Version: 1.0
 */
public class ToKafkaResult implements Callback {
    private Logger log = LoggerFactory.getLogger(getClass());
    private ProducerRecord<FileInfo, byte[]> record;

    public ToKafkaResult(ProducerRecord<FileInfo, byte[]> record) {
        this.record = record;
    }

    @Override
    public void onCompletion(RecordMetadata metadata, Exception exception) {
        if (exception == null) {
            return;
        }
        FileInfo fileInfo = record.key();
        String topic = record.topic();
        int partition = record.partition();
        long offset = metadata.offset();
        log.error("error file: {}, segmentId: {}, topic: {}, partition: {}, offset: {}, exception: {}",
                fileInfo.getFilePath(), fileInfo.getSegmentId(), topic, partition, offset, ThrowableUtil.stackTraceToString(exception));
    }
}
