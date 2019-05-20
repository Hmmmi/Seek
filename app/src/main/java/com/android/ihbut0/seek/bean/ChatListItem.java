package com.android.ihbut0.seek.bean;

import android.support.annotation.NonNull;

import com.android.ihbut0.seek.dao.FriendDAO;
import com.android.ihbut0.seek.dao.MessageDAO;
import com.android.ihbut0.seek.info.AllInformation;
import com.android.ihbut0.seek.utils.Util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatListItem extends Friend implements Comparable<ChatListItem>{

    private boolean isLastSend;//true表示本地用户最后发送
    private String lastMsgCtx;
    private String lastTime;
    private int msgUnread;

//    public ChatListItem(){}

    public ChatListItem(String chatAccount){
        Friend friend = FriendDAO.getFriend(chatAccount);
        super.setAccount(chatAccount);
        super.setHeadImg(friend.getHeadImg());
        super.setLabel(friend.getLabel());
    }

    public String getChatAccount() {
        return super.getAccount();
    }

    public int getChatHeadImg() {
        return super.getHeadImg();
    }

    public String getChatLabel() {
        return super.getLabel();
    }

    public boolean isLastSend() {
        return isLastSend;
    }

    public void setLastSend(boolean lastSend) {
        isLastSend = lastSend;
    }

    public String getLastMsg() {
        return lastMsgCtx;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsgCtx = lastMsg;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public int getMsgUnread() {
        return msgUnread;
    }

    public void setMsgUnread(int msgUnread) {
        this.msgUnread = msgUnread;
    }

    @Override
    public int compareTo(@NonNull ChatListItem o) {
        int res = Util.compareTime(lastTime, o.getLastTime());
        return -res;
    }

    @Override
    public String toString() {
        return "哈哈 item:("+getChatLabel()+") "+lastMsgCtx+" "+lastTime;
    }
}
