package com.android.ihbut0.seek.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class LocalDataUtil {

    public static final String  DEFAULT_VALUE = "DEFAULT";

    /**
     * 保存一组数据
     *
     * @param key
     * @param data
     * @param PREFER_NAME
     * @param context
     */
    public static void saveData(String key, String data, String PREFER_NAME, Context context){
        SharedPreferences preferences = context.getSharedPreferences(PREFER_NAME,
                MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, data);
        editor.commit();    //提交
    }

    /**
     * 保存单个数据
     * @param keys
     * @param datas
     * @param PREFER_NAME
     * @param context
     */
    public static void saveDatas(String keys[], String datas[], String PREFER_NAME, Context context){
        SharedPreferences preferences = context.getSharedPreferences(PREFER_NAME,
                MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        int len = keys.length;
        for (int i = 0; i < len; i++) {
            editor.putString(keys[i], datas[i]);
        }

        editor.commit();    //提交
    }

    /**
     * 获取一个数据
     *
     * @param key
     * @param PREFER_NAME
     * @param context
     * @return
     */
    public static String getData(String key, String PREFER_NAME, Context context){
        SharedPreferences preferences = context.getSharedPreferences(PREFER_NAME,
                MODE_PRIVATE);
        return preferences.getString(key, DEFAULT_VALUE);
    }

    /**
     * 获取一组数据
     *
     * @param keys
     * @param PREFER_NAME
     * @param context
     * @return
     */
    public static String[] getDatas(String keys[], String PREFER_NAME, Context context){
        SharedPreferences preferences = context.getSharedPreferences(PREFER_NAME,
                MODE_PRIVATE);
        int len = keys.length;
        String[] datas = new String[len];
        for (int i = 0; i < len; i++) {
            datas[i] = preferences.getString(keys[i], DEFAULT_VALUE);
        }
        return datas;
    }

}
