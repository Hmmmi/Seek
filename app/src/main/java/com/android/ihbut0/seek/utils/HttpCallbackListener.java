package com.android.ihbut0.seek.utils;

public interface HttpCallbackListener {

    void onFinish(String response);

    void onError(Exception e);

}
