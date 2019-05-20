package com.android.ihbut0.seek.bean;

import java.util.UUID;

public class Message {

    private UUID msgID;//消息ID
    private String sendAccount;//发送人账号
    private String receiveAccount;//接收人账号
    private String sendTime;//发送时间
    private int msgType;//消息类型
    private String msgCtx;//消息内容

    public Message(UUID msgID, String sendAccount, String receiveAccount,
                   String sendTime, int msgType, String msgCtx){
        this.msgID = msgID;
        this.sendAccount = sendAccount;
        this.receiveAccount = receiveAccount;
        this.sendTime = sendTime;
        this.msgType = msgType;
        this.msgCtx = msgCtx;
    }

    public UUID getMsgID() {
        return msgID;
    }

    public void setMsgID(UUID msgID) {
        this.msgID = msgID;
    }

    public String getSendAccount() {
        return sendAccount;
    }

    public void setSendAccount(String sendAccount) {
        this.sendAccount = sendAccount;
    }

    public String getReceiveAccount() {
        return receiveAccount;
    }

    public void setReceiveAccount(String receiveAccount) {
        this.receiveAccount = receiveAccount;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getMsgCtx() {
        return msgCtx;
    }

    public void setMsgCtx(String msgCtx) {
        this.msgCtx = msgCtx;
    }

    @Override
    public String toString() {
        return " 消息 "+sendAccount+" to "+receiveAccount+"【"+ msgCtx+"】";
    }
}
