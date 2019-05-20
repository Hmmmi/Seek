package com.android.ihbut0.seek.dao;

import android.graphics.drawable.Drawable;

public class HeadImgDAO {

    private static Drawable[] headImgs;

    public static void setHeadImgs(Drawable[] headImgs){
        HeadImgDAO.headImgs = headImgs;
    }

    public static Drawable getHeadImg(int i) {
        return headImgs[i];
    }

}
