package com.agmbat.baidumap;

import java.util.ArrayList;
import java.util.List;

import com.agmbat.android.SystemManager;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * 百度Location
 */
public class BDLocationManager {

    /**
     * 百度位置管理
     */
    private LocationClient mLocationClient;

    /**
     * 是否开始记录位置变化
     */
    private boolean mRecordLocation;

    /**
     * 记录最后一次位置
     */
    private BDLocation mBDLocation;

    /**
     * 内部位置监听器
     */
    private MyLocationListener mMyLocationListener = new MyLocationListener();

    /**
     * 对外调用的位置监听器
     */
    private List<BDLocationListener> mListeners = new ArrayList<BDLocationListener>();

    public BDLocationManager() {
        mLocationClient = new LocationClient(SystemManager.getContext());
        LocationClientOption option = initLocation();
        mLocationClient.setLocOption(option);
    }

    /**
     * 添加定位监听器
     *
     * @param l
     */
    public void addBDLocationListener(BDLocationListener l) {
        if (!mListeners.contains(l)) {
            mListeners.add(l);
        }
    }

    /**
     * 移除定位监听器
     *
     * @param l
     */
    public void removeBDLocationListener(BDLocationListener l) {
        if (mListeners.contains(l)) {
            mListeners.remove(l);
        }
    }

    /**
     * Returns a Location indicating the data from the last known
     * location fix obtained from the given provider.
     *
     * @return
     */
    public BDLocation getLastKnownLocation() {
        return mLocationClient.getLastKnownLocation();
    }

    /**
     * 获取位置
     *
     * @return
     */
    public BDLocation getLocation() {
        BDLocation location = getCurrentLocation();
        if (location == null) {
            location = getLastKnownLocation();
        }
        return location;
    }

    /**
     * 返回当前记录的位置
     *
     * @return
     */
    public BDLocation getCurrentLocation() {
        return mBDLocation;
    }

    /**
     * 开始或停止记录位置
     *
     * @param recordLocation
     */
    public void recordLocation(boolean recordLocation) {
        if (mRecordLocation != recordLocation) {
            mRecordLocation = recordLocation;
            if (recordLocation) {
                startReceivingLocationUpdates();
            } else {
                stopReceivingLocationUpdates();
            }
        }
    }

    /**
     * 开始记录位置
     */
    private void startReceivingLocationUpdates() {
        mLocationClient.registerLocationListener(mMyLocationListener);
        mLocationClient.start();
    }

    /**
     * 停止记录位置
     */
    private void stopReceivingLocationUpdates() {
        mLocationClient.unRegisterLocationListener(mMyLocationListener);
        mLocationClient.stop();
    }

    private LocationClientOption initLocation() {
        LocationClientOption option = new LocationClientOption();
        // 设置坐标类型
        option.setCoorType("bd09ll");
        // 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setScanSpan(1000);
        // 可选，设置是否需要地址信息，默认不需要
        option.setIsNeedAddress(false);
        // 可选，默认false,设置是否使用gps
        option.setOpenGps(true);
        // 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setLocationNotify(true);
        // 可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setIgnoreKillProcess(true);
        return option;
    }

    /**
     * 内部实现实时位置回调监听
     */
    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            for (BDLocationListener l : mListeners) {
                l.onReceiveLocation(bdLocation);
            }
            if (bdLocation != null) {
                mBDLocation = bdLocation;
            }
        }
    }

}
