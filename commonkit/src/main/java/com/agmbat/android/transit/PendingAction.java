package com.agmbat.android.transit;

import android.content.Context;
import android.content.Intent;

/**
 * 正在执行的action
 */
public interface PendingAction {

    /**
     * 处理Activity回来后事情
     *
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int resultCode, Intent data);

    /**
     * 获取启动真实的Activity的Intent
     *
     * @return
     */
    public Intent getActionIntent(Context context);
}
