package com.android.ihbut0.seek.bean;


public class Interest {

    private String intentUUID;
    private String intentCtx;

    public Interest(String intentUUID, String intentCtx){
        this.intentUUID = intentUUID;
        this.intentCtx = intentCtx;
    }

    public String getIntentUUID() {
        return intentUUID;
    }

    public void setIntentUUID(String intentUUID) {
        this.intentUUID = intentUUID;
    }

    public String getIntentCtx() {
        return intentCtx;
    }

    public void setIntentCtx(String intentCtx) {
        this.intentCtx = intentCtx;
    }
}
