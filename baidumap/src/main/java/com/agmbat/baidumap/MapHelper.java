package com.agmbat.baidumap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.text.TextUtils;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.CoordUtil;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.baidu.mapapi.utils.OpenClientUtil;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;

import java.util.HashMap;
import java.util.Map;

/**
 * 地图辅助类
 */
public class MapHelper {

    /**
     * 转化成Location
     *
     * @param bdLocation
     * @return
     */
    public static Location create(BDLocation bdLocation) {
        if (bdLocation != null) {
            Location location = new Location("");
            location.setLatitude(bdLocation.getLatitude());
            location.setLongitude(bdLocation.getLongitude());
            return location;
        }
        return null;
    }

    /**
     * 启动百度地图公交路线规划
     */
    public static void startRoutePlanTransit(Context context, LatLng ptStart, LatLng ptEnd, String endName) {
        // 构建 route搜索参数
        RouteParaOption para = new RouteParaOption();
        para.startPoint(ptStart);
        para.endPoint(ptEnd);
        if (!TextUtils.isEmpty(endName)) {
            para.endName(endName);
        }
        para.busStrategyType(RouteParaOption.EBusStrategyType.bus_recommend_way);
        try {
            BaiduMapRoutePlan.openBaiduMapTransitRoute(para, context);
        } catch (Exception e) {
            e.printStackTrace();
            showDialog(context);
        }
    }

    /**
     * 提示未安装百度地图app或app版本过低
     */
    private static void showDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("您尚未安装百度地图app或app版本过低，点击确认安装？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                OpenClientUtil.getLatestBaiduMapApp(context);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 本应是5-30,但由于采用int型无法标识出小数点，所以乘以10
     */
    public static final int MIN_PROGRESS = 50;
    public static final int MAX_PROGRESS = 300;

    /**
     * 对应5-30倍时level
     */
    public static final float MAX_LEVEL = 13.842299f;
    public static final float MIN_LEVEL = 11.2561f;

    /**
     * 获取地图level
     *
     * @param region
     * @return
     */
    public static float getMapLevel(float region) {
        float minProgress = MIN_PROGRESS / 10;
        float maxProgress = MAX_PROGRESS / 10;
        return MAX_LEVEL - (region - minProgress) * (MAX_LEVEL - MIN_LEVEL) / (maxProgress - minProgress);
    }

    /**
     * 获取区域
     *
     * @param progress
     * @return
     */
    public static float getRegion(int progress) {
        float region = progress / 10.0f;
        return region;
    }

    public static int getProgress(float region) {
        return (int) (region * 10);
    }

    /**
     * 获取Delta经纬度
     *
     * @param latLng 当前位置
     * @param region 距离当前位置,以米为单位
     * @return delta经纬度
     */
    public static LatLng getDeltaLatLng(LatLng latLng, int region) {
        int delta = CoordUtil.getMCDistanceByOneLatLngAndRadius(latLng, region);
        GeoPoint geoPoint = CoordUtil.ll2mc(latLng);
        double latitudeE6 = geoPoint.getLatitudeE6();
        double longitudeE6 = geoPoint.getLongitudeE6();
        GeoPoint pointA = new GeoPoint(latitudeE6 + delta, longitudeE6 + delta);
        LatLng latLngA = CoordUtil.mc2ll(pointA);
        GeoPoint pointB = new GeoPoint(latitudeE6 - delta, longitudeE6 - delta);
        LatLng latLngB = CoordUtil.mc2ll(pointB);
        double deltaLat = Math.abs(latLngA.latitude - latLngB.latitude);
        double deltaLon = Math.abs(latLngA.longitude - latLngB.longitude);
        LatLng deltaLatLng = new LatLng(deltaLat, deltaLon);
        return deltaLatLng;
    }
}
