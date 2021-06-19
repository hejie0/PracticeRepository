package netty.demo.filesync.kafka;

import com.google.gson.ExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.netty.util.internal.ThrowableUtil;
import netty.demo.filesync.scanner.FileInfo;
import netty.demo.filesync.task.Task;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: hejie
 * @Date: 2021/5/31 14:14
 * @Version: 1.0
 */
public class FileInfoSerializer implements Serializer<FileInfo> {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public byte[] serialize(String topic, FileInfo fileInfo) {
        try {
            ExclusionStrategy exclusionStrategy = new FieldExclusionStrategy(Task.class);
            Gson gson = new GsonBuilder()
                    .addDeserializationExclusionStrategy(exclusionStrategy)
                    .create();
            String jsonStr = gson.toJson(fileInfo);
            return jsonStr.getBytes();
        } catch (Exception e) {
            log.error("Error when serializing FileInfo to byte[], error: {}", ThrowableUtil.stackTraceToString(e));
            throw e;
        }
    }
}
