package com.android.ihbut0.seek.utils;

import android.location.GpsStatus;
import android.location.Location;
import android.os.Bundle;

public interface SeekLocationListener {

    void updateLastLocation(Location location);

    void updateLocation(Location location);// 位置信息发生改变

    void updateStatus(String provider, int status, Bundle extras);// 位置状态发生改变

    void updateGpsStatus(GpsStatus gpsStatus);// GPS状态发生改变

}
