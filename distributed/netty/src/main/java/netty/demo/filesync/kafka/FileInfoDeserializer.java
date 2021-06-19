package netty.demo.filesync.kafka;

import com.google.gson.ExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.netty.util.internal.ThrowableUtil;
import netty.demo.filesync.scanner.FileInfo;
import netty.demo.filesync.task.Task;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: hejie
 * @Date: 2021/6/3 13:26
 * @Version: 1.0
 */
public class FileInfoDeserializer implements Deserializer<FileInfo> {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public FileInfo deserialize(String topic, byte[] data) {
        try {
            ExclusionStrategy exclusionStrategy = new FieldExclusionStrategy(Task.class);
            Gson gson = new GsonBuilder()
                    .addDeserializationExclusionStrategy(exclusionStrategy)
                    .create();
            String jsonStr = new String(data);
            FileInfo fileInfo = gson.fromJson(jsonStr, FileInfo.class);
            return fileInfo;
        } catch (Exception e) {
            log.info("Error when deserializing FileInfo to byte[] , error:{}", ThrowableUtil.stackTraceToString(e));
            throw e;
        }
    }
}
