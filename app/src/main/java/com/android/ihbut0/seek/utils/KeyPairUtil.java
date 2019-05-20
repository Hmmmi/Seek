package com.android.ihbut0.seek.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.math.BigInteger;
import java.util.Map;

public class KeyPairUtil {

    private static Map<String, BigInteger> keyPair;

    public static void setKeyPair(String keyJson){
        keyPair = new Gson().fromJson(keyJson, new TypeToken<Map<String, BigInteger>>() {}.getType());
    }

    public static Map<String, BigInteger> getKeyPair() {
        return keyPair;
    }

    public static void setKeyPair(Map<String, BigInteger> keyPair) {
        KeyPairUtil.keyPair = keyPair;
    }

    public static Map<String, BigInteger> getKeyPair(String json){
        return new Gson().fromJson(json, new TypeToken<Map<String, BigInteger>>() {}.getType());
    }
}
