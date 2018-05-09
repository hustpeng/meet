package com.agmbat.meetyou.discovery.search;

import android.content.Context;
import android.content.Intent;

import com.agmbat.meetyou.discovery.filter.FilterLoader;

public class DiscoveryHelper {

    private static final int DISCOVER_TYPE_NEAR_BY_USERS = 1;
    private static final int DISCOVER_TYPE_LOVER = 2;
    private static final int DISCOVER_TYPE_HOBBY = 3;
    private static final int DISCOVER_TYPE_BIRTHPLACE = 4;
    private static final int DISCOVER_TYPE_FILTER = 5;

    private static final String TYPE = "type";

    /**
     * 创建数据加载器
     *
     * @param intent
     * @return
     */
    public static DiscoveryLoader getLoader(Intent intent) {
        int type = intent.getIntExtra(TYPE, DISCOVER_TYPE_NEAR_BY_USERS);
        if (type == DISCOVER_TYPE_NEAR_BY_USERS) {
            return new NearbyUsersLoader();
        } else if (type == DISCOVER_TYPE_LOVER) {
            return new LoversLoader();
        } else if (type == DISCOVER_TYPE_HOBBY) {
            return new HobbyLoader();
        } else if (type == DISCOVER_TYPE_BIRTHPLACE) {
            return new BirthplaceLoader();
        } else if (type == DISCOVER_TYPE_FILTER) {
            return new FilterLoader();
        }
        return null;
    }

    /**
     * 打开附近的人页面
     *
     * @param context
     */
    public static void openNearByUsers(Context context) {
        Intent intent = new Intent(context, DiscoveryActivity.class);
        intent.putExtra(TYPE, DISCOVER_TYPE_NEAR_BY_USERS);
        context.startActivity(intent);
    }


    /**
     * 打开找恋人页面
     *
     * @param context
     */
    public static void openLover(Context context) {
        Intent intent = new Intent(context, DiscoveryActivity.class);
        intent.putExtra(TYPE, DISCOVER_TYPE_LOVER);
        context.startActivity(intent);
    }


    /**
     * 打开找玩伴页面
     *
     * @param context
     */
    public static void openHobby(Context context) {
        Intent intent = new Intent(context, DiscoveryActivity.class);
        intent.putExtra(TYPE, DISCOVER_TYPE_HOBBY);
        context.startActivity(intent);
    }

    /**
     * 打开找老乡页面
     *
     * @param context
     */
    public static void openBirthplace(Context context) {
        Intent intent = new Intent(context, DiscoveryActivity.class);
        intent.putExtra(TYPE, DISCOVER_TYPE_BIRTHPLACE);
        context.startActivity(intent);
    }

    /**
     * 打开搜索界面
     *
     * @param context
     */
    public static void openFilter(Context context) {
        Intent intent = new Intent(context, DiscoveryActivity.class);
        intent.putExtra(TYPE, DISCOVER_TYPE_FILTER);
        context.startActivity(intent);
    }
}
