package com.agmbat.baidumap;

import android.app.Activity;
import android.os.Bundle;

import com.agmbat.android.utils.UiUtils;
import com.agmbat.map.Maps;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;

import java.util.List;

/**
 * Map控件
 */
public class MapActivity extends Activity {

    /**
     * 地图view
     */
    private MapView mMapView;

    /**
     * 地图对象
     */
    private BaiduMap mBaiduMap;

    /**
     * UI配置
     */
    private UiSettings mUiSettings;

    /**
     * 当前中心点
     */
    private BitmapDescriptor mBitmapCenterDes = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);

    /**
     * 当前中心点标记
     */
    protected Marker mPlaceMarker;

    /**
     * Location管理
     */
    private BDLocationManager mLocationManager;

    private BDLocationListener mBDLocationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(final BDLocation location) {
            if (location != null) {
                UiUtils.post(new Runnable() {
                    @Override
                    public void run() {
                        updateLocalOverlay(location);
                        stopRecordLocation();
                    }
                });
            }
        }
    };

    private MapConfig mMapConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity_map);
        mMapView = (MapView) findViewById(R.id.map_view);
        mBaiduMap = mMapView.getMap();
        mUiSettings = mBaiduMap.getUiSettings();

        setupMapView();

        mMapConfig = Maps.getAndRemoveMapConfig(getIntent());
        if (mMapConfig.isShowCurrentLocation()) {
            mLocationManager = new BDLocationManager();
            mLocationManager.recordLocation(true);
            mLocationManager.addBDLocationListener(mBDLocationListener);
        }

        LatLng center = mMapConfig.getMapCenter();
        updateMapRegion(center);

        // 标记marker
        List<MarkerOptions> markerOptionsList = mMapConfig.getMarkerList();
        for (MarkerOptions options : markerOptionsList) {
            addMarker(options);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // activity 暂停时同时暂停地图控件
        mMapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        // activity 恢复时同时恢复地图控件
        mMapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // activity 销毁时同时销毁地图控件
        mMapView.onDestroy();
        stopRecordLocation();
    }

    private void setupMapView() {
        // 是否显示比例尺
        mMapView.showScaleControl(false);
        // 是否显示缩放控件
        mMapView.showZoomControls(false);
        // 是否启用缩放手势
        mUiSettings.setZoomGesturesEnabled(true);
        // 是否启用平移手势
        mUiSettings.setScrollGesturesEnabled(true);
        // 是否启用旋转手势
        mUiSettings.setRotateGesturesEnabled(false);
        // 是否启用俯视手势
        mUiSettings.setOverlookingGesturesEnabled(false);
        // 是否启用指南针图层
        mUiSettings.setCompassEnabled(false);
        // 是否显示底图默认标注
        mBaiduMap.showMapPoi(true);
    }

    /**
     * 标识当前点
     */
    private void updateLocalOverlay(BDLocation location) {
        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
        if (mPlaceMarker != null) {
            mPlaceMarker.setPosition(position);
            return;
        }
        BitmapDescriptor icon = mBitmapCenterDes;
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(position);
        markerOptions.anchor(0.5f, 0.5f);
        markerOptions.icon(icon);
        markerOptions.zIndex(9);
        mPlaceMarker = (Marker) mBaiduMap.addOverlay(markerOptions);
    }

    /**
     * 添加marker
     *
     * @param markerOptions
     */
    private void addMarker(MarkerOptions markerOptions) {
        mBaiduMap.addOverlay(markerOptions);
    }

    /**
     * 停止记录位置
     */
    private void stopRecordLocation() {
        if (mLocationManager != null) {
            mLocationManager.removeBDLocationListener(mBDLocationListener);
            mLocationManager.recordLocation(false);
        }
    }

    /**
     * 更新地图显示区域
     */
    private void updateMapRegion(LatLng center) {
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(center, 13);
        mBaiduMap.setMapStatus(u);
    }
}


