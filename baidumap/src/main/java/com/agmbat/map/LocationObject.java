package com.agmbat.map;

import android.content.Intent;

public class LocationObject {

    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_ADDRESS = "address";

    public double mLatitude;
    public double mLongitude;
    public String mAddress;

    /**
     * 将对象添加到Intent中, 对于小量的字段数据, 直接采用key会比Serializable性能要好
     *
     * @param intent
     * @param location
     */
    public static void putToIntent(Intent intent, LocationObject location) {
        intent.putExtra(KEY_LATITUDE, location.mLatitude);
        intent.putExtra(KEY_LONGITUDE, location.mLongitude);
        intent.putExtra(KEY_ADDRESS, location.mAddress);
    }

    /**
     * 从Intent中取出对象
     *
     * @param intent
     * @return
     */
    public static LocationObject fromIntent(Intent intent) {
        LocationObject location = new LocationObject();
        location.mLatitude = intent.getDoubleExtra(KEY_LATITUDE, 0);
        location.mLongitude = intent.getDoubleExtra(KEY_LONGITUDE, 0);
        location.mAddress = intent.getStringExtra(KEY_ADDRESS);
        return location;
    }
}
