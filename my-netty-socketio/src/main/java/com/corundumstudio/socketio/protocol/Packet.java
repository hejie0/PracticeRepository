package com.corundumstudio.socketio.protocol;

import com.corundumstudio.socketio.namespace.Namespace;
import io.netty.buffer.ByteBuf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Packet implements Serializable {

    private static final long serialVersionUID = -3629044018953144761L;

    private PacketType type;
    private PacketType subType;
    private Long ackId;
    private String name;
    private String nsp = Namespace.DEFAULT_NAME;
    private Object data;

    private ByteBuf dataSource;
    private int attachmentsCount;
    private List<ByteBuf> attachments = Collections.emptyList();

    public Packet() {
    }

    public Packet(PacketType type) {
        this.type = type;
    }

    public PacketType getSubType() {
        return subType;
    }

    public void setSubType(PacketType subType) {
        this.subType = subType;
    }

    public PacketType getType() {
        return type;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public <T> T getData() {
        return (T) data;
    }

    public Packet withNsp(String namespace) {
        if (this.nsp.equalsIgnoreCase(namespace)) {
            return this;
        } else {
            Packet newPacket = new Packet(this.type);
            newPacket.setSubType(this.subType);
            newPacket.setAckId(this.ackId);
            newPacket.setName(this.name);
            newPacket.setNsp(namespace);
            newPacket.setData(this.data);
            newPacket.setDataSource(this.dataSource);
            newPacket.attachmentsCount = this.attachmentsCount;
            newPacket.attachments = this.attachments;
            return newPacket;
        }
    }

    public String getNsp() {
        return nsp;
    }

    public void setNsp(String nsp) {
        this.nsp = nsp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getAckId() {
        return ackId;
    }

    public void setAckId(Long ackId) {
        this.ackId = ackId;
    }

    public boolean isAckRequested() {
        return getAckId() != null;
    }

    public ByteBuf getDataSource() {
        return dataSource;
    }

    public void setDataSource(ByteBuf dataSource) {
        this.dataSource = dataSource;
    }

    public void initAttachments(int attachmentsCount) {
        this.attachmentsCount = attachmentsCount;
        this.attachments = new ArrayList<ByteBuf>(attachmentsCount);
    }

    public void addAttachment(ByteBuf attachment){
        if (this.attachments.size() < attachmentsCount){
            this.attachments.add(attachment);
        }
    }

    public List<ByteBuf> getAttachments() {
        return attachments;
    }

    public boolean hasAttachments(){
        return attachmentsCount != 0;
    }

    public boolean isAttachmentsLoaded(){
        return this.attachments.size() == attachmentsCount;
    }

    @Override
    public String toString() {
        return "Packet{" +
                "type=" + type +
                ", ackId=" + ackId +
                '}';
    }
}
