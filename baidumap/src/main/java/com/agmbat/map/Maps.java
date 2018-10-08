package com.agmbat.map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.agmbat.android.transit.ActionHelper;
import com.agmbat.android.transit.PendingAction;
import com.agmbat.baidumap.MapActivity;
import com.agmbat.baidumap.MapConfig;
import com.agmbat.baidumap.position.LocationActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理地图相关的接口
 */
public class Maps {


    /**
     * 传递的key
     */
    private static final String KEY_CONFIG_ID = "config_id";

    /**
     * 缓存
     */
    private static final Map<String, MapConfig> CONFIG_CACHE = new HashMap<>();


    /**
     * 获取位置
     *
     * @param context
     */
    public static void getLocation(Context context, LocationCallback callback) {
        ActionHelper.request(context, new LocationAction(callback));
    }

    private static class LocationAction implements PendingAction {

        private final LocationCallback mLocationCallback;

        public LocationAction(LocationCallback callback) {
            mLocationCallback = callback;
        }

        @Override
        public void onActivityResult(int resultCode, Intent data) {
            if (resultCode == Activity.RESULT_OK) {
                LocationObject object = LocationObject.fromIntent(data);
                if (mLocationCallback != null) {
                    mLocationCallback.callback(object);
                }
            }
        }

        @Override
        public Intent getActionIntent(Context context) {
            return new Intent(context, LocationActivity.class);
        }
    }

    /**
     * 查看地图
     *
     * @param context
     * @param config
     */
    public static void viewMap(Context context, MapConfig config) {
        String key = String.valueOf(context.hashCode());
        CONFIG_CACHE.put(key, config);
        Intent intent = new Intent(context, MapActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KEY_CONFIG_ID, key);
        context.startActivity(intent);
    }

    /**
     * 获取config
     *
     * @param intent
     * @return
     */
    public static MapConfig getMapConfig(Intent intent) {
        String key = intent.getStringExtra(KEY_CONFIG_ID);
        return CONFIG_CACHE.get(key);
    }

    /**
     * 获取config并移除缓存数据
     *
     * @param intent
     * @return
     */
    public static MapConfig getAndRemoveMapConfig(Intent intent) {
        String key = intent.getStringExtra(KEY_CONFIG_ID);
        return CONFIG_CACHE.remove(key);
    }
}
