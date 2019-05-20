package com.android.ihbut0.seek.dao;

import com.android.ihbut0.seek.bean.Friend;
import com.android.ihbut0.seek.bean.Message;
import com.android.ihbut0.seek.info.AllInformation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MessageDAO {

    public static List<Message> getAllMessages(){
        List<Message> messages = new ArrayList<Message>();
//        String localAccount = UserDAO.getLocalUser().getAccount();
//
//        String msgString = "";
//
//        Message msg1 = new Message(UUID.randomUUID(), "18872783849", localAccount, "2018-10-04 11:07:20",
//                0, "(49):"+msgString+"(消息1)");
//        Message msg2 = new Message(UUID.randomUUID(), "18872783849", localAccount, "2018-10-04 11:08:20",
//                0, "(49):消息2");
//        Message msg3 = new Message(UUID.randomUUID(), localAccount, "18872783849", "2018-10-04 11:10:20",
//                0, "(我啊-49):消息3");
//        Message msg4 = new Message(UUID.randomUUID(), "18872783849", localAccount, "2018-10-04 11:20:20",
//                0, "(49):消息4");
//
//        Message msg5 = new Message(UUID.randomUUID(), localAccount, "18872783859", "2018-10-04 11:23:20",
//                0, "(我啊-59):消息5");
//        Message msg6 = new Message(UUID.randomUUID(), "18872783859", localAccount, "2018-10-04 11:25:20",
//                0, "(59):消息6");
//        Message msg7 = new Message(UUID.randomUUID(), "18872783859", localAccount, "2018-10-04 13:21:20",
//                0, "(59):消息7");
//
//        Message msg8 = new Message(UUID.randomUUID(), localAccount, "18872783869", "2018-10-04 15:07:20",
//                0, "(我啊-69):消息8");
//        Message msg9 = new Message(UUID.randomUUID(), "18872783869", localAccount, "2018-10-04 16:07:20",
//                0, "(69):消息9");
//        Message msg10 = new Message(UUID.randomUUID(), localAccount, "18872783859", "2018-10-04 17:07:20",
//                0, "(我啊-69):消息10");
//
//        messages.add(msg1);
//        messages.add(msg2);
//        messages.add(msg3);
//        messages.add(msg4);
//        messages.add(msg5);
//        messages.add(msg6);
//        messages.add(msg7);
//        messages.add(msg8);
//        messages.add(msg9);
//        messages.add(msg10);

        return messages;
    }

    public static List<Message> getMessages(String friendAccount){
        String localAccount = UserDAO.getLocalUser().getAccount();
        List<Message> allMessages = getAllMessages();
        List<Message> messages = new ArrayList<Message>();

        for (Message m : allMessages){
            if ( m.getSendAccount().equals(friendAccount)&& m.getReceiveAccount().equals(localAccount) ){
                messages.add(m);
            } else if ( m.getSendAccount().equals(localAccount)&& m.getReceiveAccount().equals(friendAccount) ){
                messages.add(m);
            }
        }

        return messages;
    }

    public static Message getLastMsg(String friendAccount){
        List<Message> messages = getMessages(friendAccount);
        int size = messages.size();
        if ( size > 0 ){
            return messages.get(size-1);
        }
        return null;
    }

}
