package com.agmbat.baidumap;

import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * 地图查看配置
 */
public class MapConfig {

    /**
     * 是否标记当前点
     */
    private boolean mIsShowCurrent;

    /**
     * 地图中心位置
     */
    private LatLng mMapCenter;

    /**
     * marker list
     */
    private List<MarkerOptions> mMarkerList = new ArrayList<>();

    /**
     * 设置是否显示当前位置
     *
     * @param show
     */
    public void setIsShowCurrent(boolean show) {
        mIsShowCurrent = show;
    }

    /**
     * 是否显示当前位置点
     *
     * @return
     */
    public boolean isShowCurrentLocation() {
        return mIsShowCurrent;
    }

    /**
     * 获取当前地图中心点
     *
     * @return
     */
    public LatLng getMapCenter() {
        return mMapCenter;
    }

    public void setMapCenter(LatLng center) {
        mMapCenter = center;
    }

    public void setMarkerList(List<MarkerOptions> list) {
        mMarkerList = list;
    }

    /**
     * 获取maker列表
     *
     * @return
     */
    public List<MarkerOptions> getMarkerList() {
        return mMarkerList;
    }
}
