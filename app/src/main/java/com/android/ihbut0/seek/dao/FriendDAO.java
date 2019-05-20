package com.android.ihbut0.seek.dao;

import android.util.Log;

import com.android.ihbut0.seek.R;
import com.android.ihbut0.seek.bean.Friend;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class FriendDAO {

    private static List<Friend> friends;

    public static List<Friend> getFriends(){

        return friends;
    }

    public static void setFriends(List<Friend> friends){
        Iterator<Friend> iterator = friends.iterator();
        while (iterator.hasNext()){
            Friend friend = iterator.next();
            //friend.setHeadImg(R.drawable.headimg2);
        }
        FriendDAO.friends = friends;
    }


    public static Friend getFriend(String friendAccount){

        for (Friend f : friends){
            if (f.getAccount().equals(friendAccount)){
                return  f;
            }
        }
        return null;
    }

}
