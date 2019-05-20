package com.android.ihbut0.seek.bean;

import com.android.ihbut0.seek.dao.UserDAO;

public class NearbyUser extends User{

    private float distance;
    private float index;

//    public NearbyUser(String nearbyAccount, float distance, float index){
//
//        User user = UserDAO.getUser(nearbyAccount);
//        super.setAccount(user.getAccount());
//        super.setHeadImg(user.getHeadImg());
//        super.setNickname(user.getNickname());
//        this.distance = distance;
//        this.index = index;
//    }

    public float getDistance() {
        return distance;
    }

    public float getIndex() {
        return index;
    }

}
