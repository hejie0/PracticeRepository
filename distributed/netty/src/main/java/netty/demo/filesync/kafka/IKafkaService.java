package netty.demo.filesync.kafka;

import com.google.inject.ImplementedBy;
import netty.demo.filesync.core.IService;
import netty.demo.filesync.scanner.FileInfo;
import netty.demo.filesync.task.Task;

import java.util.concurrent.ExecutorService;

/**
 * @Author: hejie
 * @Date: 2021/6/1 9:33
 * @Version: 1.0
 */
@ImplementedBy(KafkaServiceImpl.class)
public interface IKafkaService extends IService {

    void startSendToKafka(Task task);
    void stopSendToKafka(Task task);
    void sendToKafka(FileInfo fileInfo, byte[] fileByte);
    void close();
    ExecutorService getSendExecutor(int taskId);

}
