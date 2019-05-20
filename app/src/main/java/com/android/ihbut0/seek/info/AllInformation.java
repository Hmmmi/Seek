package com.android.ihbut0.seek.info;

import android.location.Location;

import com.android.ihbut0.seek.bean.ChatListItem;
import com.android.ihbut0.seek.bean.Friend;
import com.android.ihbut0.seek.bean.Interest;
import com.android.ihbut0.seek.bean.Message;
import com.android.ihbut0.seek.bean.SeekUser;
import com.android.ihbut0.seek.bean.User;
import com.android.ihbut0.seek.dao.ChatListDAO;
import com.android.ihbut0.seek.dao.FriendDAO;
import com.android.ihbut0.seek.dao.InterestDAO;
import com.android.ihbut0.seek.dao.MessageDAO;
import com.android.ihbut0.seek.dao.UserDAO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AllInformation {
    public static final String APP_KEY = "2826a3379f736";
    public static final String APP_SECRET = "5d0343fdfc58fd4fd723089322c37b3e";

    public static final String IP = "192.168.123.176";
    public static final String originAddress = "https://"+IP+":8443"+"//SeekServer/servlet/";

//    private static User self;//本机账户信息
//    private static List<Friend> friends;//好友信息
//    private static List<ChatListItem> chatListItems;//聊天列表信息
////    private static Map<String,List<Message>> msgMap;//与某好友消息信息
////    private static List<Interest> interests;//兴趣
//    private static Map<String,List<Interest>> commonInterestMap;//与好友共同兴趣
//    private static Location location;//本机位置信息
//
//    public static void notifyInfo(){
//
//        friends = new ArrayList<Friend>();
////        msgMap = new HashMap<String,List<Message>>();
//        chatListItems = new ArrayList<>();
////        interests = new ArrayList<Interest>();
//        commonInterestMap = new HashMap<String,List<Interest>>();
//
//        self = UserDAO.getLocalUser();
//        friends = FriendDAO.getFriends(self.getAccount());
//        chatListItems = ChatListDAO.getChatList();
//        Collections.sort(chatListItems);//排序
//
////        for ( Friend friend : friends ){
////            msgMap.put(friend.getAccount() , MessageDAO.getMessages(friend.getAccount()));
////        }
//        for ( Friend friend : friends ){
//            commonInterestMap.put(friend.getAccount() , InterestDAO.getCommonInterests(friend.getAccount()));
//        }
////        interests = InterestDAO.getInterests();
//        Location location = null;
//    }
//
//    public static User getSelf() {
//        return self;
//    }
//
//    public static void setSelf(User self) {
//        AllInformation.self = self;
//    }
//
//    public static List<Friend> getFriends() {
//        return friends;
//    }
//
//    public static void setFriends(List<Friend> friends) {
//        AllInformation.friends = friends;
//    }
//
//    public static void saveFriendLabel(String friendAccount, String label){
//        for ( Friend xFriend: friends ){
//            if ( xFriend.getAccount().equals(friendAccount) ){
//                xFriend.setLabel(label);
//            }
//        }
//    }
//
//    public static void removeFriend(String friendAccount){
//        int i = 0, res = friends.size();
//        for (Friend friend : friends ){
//            if (friend.getAccount().equals(friendAccount)){
//                res = i;
//            }
//            i++;
//        }
//        if ( res != friends.size() ){
//            removeChatListItem(friendAccount);
//            friends.remove(res);
//        }
//    }
//
//    public static Friend getFriend(String friendAccount){
//        for ( Friend xFriend: friends ){
//            if ( xFriend.getAccount().equals(friendAccount) ){
//                return xFriend;
//            }
//        }
//        return null;
//    }
//
//    public static List<ChatListItem> getChatListItems() {
//        Collections.sort(chatListItems);
//        return chatListItems;
//    }
//
//    public static void setChatListItems(List<ChatListItem> chatListItems) {
//        AllInformation.chatListItems = chatListItems;
//    }
//
//    /**
//     * 删除与chatAccount的聊天
//     * @param chatAccount
//     */
//    public static void removeChatListItem(String chatAccount){
//        int i = 0, res = chatListItems.size();
//        for ( ChatListItem item : chatListItems ){
//            if(item.getChatAccount().equals(chatAccount)){
//                res = i;
//            }
//            i++;
//        }
//        if ( res != chatListItems.size() ){
//            chatListItems.remove(res);
//        }
//    }
//
//    /**
//     * 与该好友的聊天是否在聊天列表中
//     * @param chatAccount
//     * @return
//     */
//    public static boolean isInChatList(String chatAccount){
//        for ( ChatListItem item : chatListItems ){
//            if ( item.getChatAccount().equals(chatAccount) ){
//                return true;
//            }
//        }
//        return false;
//    }
//
//
////    public static Map<String, List<Message>> getMsgMap() {
////        return msgMap;
////    }
//
////    public static void setMsgMap(Map<String, List<Message>> msgMap) {
////        AllInformation.msgMap = msgMap;
////    }
//
//    public static void setLocation(Location location) {
//        AllInformation.location = location;
//    }
//
//    public static Location getLocation() {
//        return location;
//    }
//
////    public static List<Interest> getInterests() {
////        return interests;
////    }
////
////    public static void setInterests(List<Interest> interests) {
////        AllInformation.interests = interests;
////    }
//
//    public static Map<String, List<Interest>> getCommonInterestMap() {
//        return commonInterestMap;
//    }
//
//    public static void setCommonInterestMap(Map<String, List<Interest>> commonInterestMap) {
//        AllInformation.commonInterestMap = commonInterestMap;
//    }
//
//    /**
//     * 返回与该好友的共同兴趣
//     * @param friendAccount
//     * @return
//     */
//    public static List<Interest> getCommonInterests(String friendAccount){
//        return commonInterestMap.get(friendAccount);
//    }

//    /**
//     * 根据兴趣ID删除兴趣
//     * @param interestID
//     */
//    public static void removeInterest(String interestID){
//        int i = 0,res = interests.size();
//        for (Interest interest : interests){
//            if ( interest.getIntentID().equals(interestID) ){
//                res = i;
//            }
//            i++;
//        }
//        interests.remove(res);
//        System.out.println("删除"+interestID+"成功！");
//    }

//    /**
//     * 根据好友账号查询最近一条消息
//     * @param chatAccount
//     * @return
//     */
//    public static Message getLastMsg(String chatAccount){
//        List<Message> messages = getMsgMap().get(chatAccount);
//        int size = messages.size();
//        if (size == 0){
//            return new Message(UUID.randomUUID(), self.getAccount(), chatAccount,
//                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
//                    0, " " );
//        }
//        return messages.get(size-1);
//    }

}
