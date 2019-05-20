package com.android.ihbut0.seek.dao;


import com.android.ihbut0.seek.R;
import com.android.ihbut0.seek.bean.User;
import com.android.ihbut0.seek.utils.KeyPairUtil;
import com.mob.MobSDK;

import java.util.ArrayList;
import java.util.List;


public class UserDAO {

    private static User localUser;

    public static void setLocalUser(User user){
        localUser = user;
        //localUser.setHeadImg(H);
        KeyPairUtil.setKeyPair(localUser.getBackground());
    }
    /**
     * 机主
     * @return
     */
    public static User getLocalUser(){
        return localUser;
    }

}
