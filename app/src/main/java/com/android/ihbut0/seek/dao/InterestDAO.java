package com.android.ihbut0.seek.dao;

import com.android.ihbut0.seek.bean.Interest;
import com.android.ihbut0.seek.utils.LocalDataUtil;

import java.util.ArrayList;
import java.util.List;

public class InterestDAO {
    private static List<Interest> interests;

    public static List<Interest> getInterests(){
        //TODO 去0
        return interests;
    }

    public static String[] getInterestStrings(){
        String[] res = new String[interests.size()];
        for (int i = 0 ; i < interests.size() ; i++ ){
            res[i] = interests.get(i).getIntentCtx();
        }
        return res;
    }

    public static void setInterests(List<Interest> interests){
        InterestDAO.interests = interests;
        //TODO 保存到本地
    }

    public static List<Interest> getCommonInterests(String friendAccount){
        List<Interest> interests = new ArrayList<>();

        interests.add(new Interest("000", "漫画") );
        interests.add(new Interest("001", "小说") );
        interests.add(new Interest("001", "电影") );
        interests.add(new Interest("001", "健身") );
        interests.add(new Interest("001", "音乐") );

        return interests;
    }

}
