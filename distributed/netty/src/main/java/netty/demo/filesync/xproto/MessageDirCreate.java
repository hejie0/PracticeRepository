package netty.demo.filesync.xproto;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageDirCreate extends Message {

    private Logger log = LoggerFactory.getLogger(getClass());
    private String dirPath;
    private String taskName;

    public MessageDirCreate(MessageType type) {
        super(MessageType.DIR_CREATE);
    }

    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public void encode(ByteBuf buf) {
        super.encode(buf);
        short dirPathLen = (short) dirPath.getBytes(CharsetUtil.UTF_8).length;
        if (log.isDebugEnabled()) {
            log.debug("encode dirPathLen:{}", dirPathLen);
        }
        buf.writeShort(dirPathLen);
        buf.writeBytes(dirPath.getBytes());

        short taskNameLen = (short) taskName.getBytes(CharsetUtil.UTF_8).length;
        if (log.isDebugEnabled()) {
            log.debug("encode taskNameLen:{}", taskNameLen);
        }
        buf.writeShort(taskNameLen);
        buf.writeBytes(taskName.getBytes());
    }

    @Override
    public void decode(ByteBuf buf) {
        super.decode(buf);
        short dirPathLen = buf.readShort();
        byte[] dirPathBytes = new byte[dirPathLen];
        buf.readBytes(dirPathBytes);
        dirPath = new String(dirPathBytes);
        if (log.isDebugEnabled()) {
            log.debug("dirPath:[{}]", dirPath);
        }

        short taskNameLen = buf.readShort();
        byte[] taskNameBytes = new byte[taskNameLen];
        buf.readBytes(taskNameBytes);
        taskName = new String(taskNameBytes);
        if (log.isDebugEnabled()) {
            log.debug("taskName:[{}]", taskName);
        }
    }

    @Override
    public String toString() {
        return "MessageDirCreate{" +
                "dirPath='" + dirPath + '\'' +
                ", taskName='" + taskName + '\'' +
                ", type=" + type +
                '}';
    }
}
