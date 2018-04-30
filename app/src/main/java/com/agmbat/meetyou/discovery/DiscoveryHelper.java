package com.agmbat.meetyou.discovery;

import android.content.Context;
import android.content.Intent;

public class DiscoveryHelper {

    private static final int DISCOVER_TYPE_NEAR_BY_USERS = 1;
    private static final int DISCOVER_TYPE_LOVER = 2;
    private static final String TYPE = "type";

    public static DiscoveryLoader getLoader(Intent intent) {
        int type = intent.getIntExtra(TYPE, DISCOVER_TYPE_NEAR_BY_USERS);
        if (type == DISCOVER_TYPE_NEAR_BY_USERS) {
            return new NearbyUsersLoader();
        } else if (type == DISCOVER_TYPE_LOVER) {
            return new LoversLoader();
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
}
