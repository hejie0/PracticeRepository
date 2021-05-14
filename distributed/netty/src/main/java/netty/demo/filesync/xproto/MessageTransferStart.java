package netty.demo.filesync.xproto;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageTransferStart extends MessageTransferSession {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private long modifyTime;
    private long size;
    private String fileName;
    private String taskName;

    public MessageTransferStart(MessageType type) {
        super(MessageType.TRANSFER_START);
    }

    @Override
    public void encode(ByteBuf buf) {
        super.encode(buf);
        buf.writeLong(modifyTime);
        buf.writeLong(size);

        short fileNameLen = (short) fileName.getBytes(CharsetUtil.UTF_8).length;
        if (log.isDebugEnabled()) {
            log.debug("encode fileNameLen: {}", fileNameLen);
        }

        buf.writeShort(fileNameLen);
        buf.writeBytes(fileName.getBytes());

        short taskNameLen = (short) taskName.getBytes(CharsetUtil.UTF_8).length;
        if (log.isDebugEnabled()) {
            log.debug("encode taskName: {}", taskName);
        }
        buf.writeShort(taskNameLen);
        buf.writeBytes(taskName.getBytes());
    }

    @Override
    public void decode(ByteBuf buf) {
        super.decode(buf);
        modifyTime = buf.readLong();
        size = buf.readLong();

        short fileNameLen = buf.readShort();
        byte[] fileNameBytes = new byte[fileNameLen];
        buf.readBytes(fileNameBytes);
        fileName = new String(fileNameBytes, CharsetUtil.UTF_8);
        if (log.isDebugEnabled()) {
            log.debug("decode fileName: [{}]", fileName);
        }

        short taskNameLen = buf.readShort();
        byte[] taskNameBytes = new byte[taskNameLen];
        buf.readBytes(taskNameBytes);
        taskName = new String(taskNameBytes, CharsetUtil.UTF_8);
        if (log.isDebugEnabled()) {
            log.debug("decode taskName: [{}]", taskName);
        }
    }

    public long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public String toString() {
        return "MessageTransferStart{" +
                "modifyTime=" + modifyTime +
                ", size=" + size +
                ", fileName='" + fileName + '\'' +
                ", taskName='" + taskName + '\'' +
                ", sessionId=" + sessionId +
                ", type=" + type +
                '}';
    }
}
