package com.android.ihbut0.seek.dao;


import com.android.ihbut0.seek.bean.ChatListItem;
import com.android.ihbut0.seek.info.AllInformation;
import com.mob.imsdk.MobIM;
import com.mob.imsdk.MobIMCallback;
import com.mob.imsdk.model.IMConversation;
import com.mob.imsdk.model.IMMessage;
import com.mob.imsdk.model.IMUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ChatListDAO {

    private static List<ChatListItem> chatList;

    public static void setChatList(List<ChatListItem> chatList) {
        ChatListDAO.chatList = chatList;
    }

    public static List<ChatListItem> getChatList() {
        if (chatList == null){
            chatList = new ArrayList<>();
        }
        return chatList;
    }

    public static List<ChatListItem> changeChatList(List<IMMessage> list) {
        List<ChatListItem> chatListTmp = new ArrayList<>();
        //收到消息时监听事件
        for (IMMessage imMessage : list) {
            String fromAccount = imMessage.getFrom();
            ChatListItem item = new ChatListItem(fromAccount);
            item.setLastMsg(imMessage.getBody());
            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(imMessage.getCreateTime());
            item.setLastTime(time);
            chatListTmp.add(item);
        }
        return chatListTmp;
    }

    public static boolean isInList(String account){
        for (ChatListItem item : chatList){
            if (item.getAccount().equals(account)){
                return true;
            }
        }
        return false;
    }
//    public bo

}
