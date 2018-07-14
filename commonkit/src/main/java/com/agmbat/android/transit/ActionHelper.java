package com.agmbat.android.transit;

import android.content.Context;

import com.agmbat.log.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Action辅助类
 */
public class ActionHelper {

    /**
     * 请求位置的缓存
     */
    private static final Map<Integer, PendingAction> PENDING_ACTION_MAP = new HashMap<>();

    /**
     * 用于自动生成RequestCode
     */
    private static int sRequestCode = 153;

    /**
     * 获取位置
     *
     * @param context
     */
    public static void request(Context context, PendingAction pendingAction) {
        int code = genRequestCode();
        PENDING_ACTION_MAP.put(code, pendingAction);
        TransitActivity.request(context, code);
    }

    /**
     * 获取PendingAction
     *
     * @param requestCode
     * @return
     */
    public static PendingAction getPendingAction(int requestCode) {
        return PENDING_ACTION_MAP.get(requestCode);
    }

    /**
     * 清除action
     */
    public static void clearAction() {
        if (PENDING_ACTION_MAP.size() != 0) {
            Log.e("onActivityResult: map size:" + PENDING_ACTION_MAP.size());
            PENDING_ACTION_MAP.clear();
        }
    }

    /**
     * 自动生成requestCode
     *
     * @return
     */
    private static int genRequestCode() {
        sRequestCode++;
        if (sRequestCode > 240) {
            sRequestCode = 150;
        }
        return sRequestCode;
    }

}
