/*
 * Copyright (C) 2015 mayimchen <mayimchen@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.agmbat.android.utils;

import java.util.List;

import android.app.ActivityManager;
import android.content.Context;

public class ThreadUtil {

    private static final String TAG = ThreadUtil.class.getSimpleName();

    /**
     * 判断是否在主进程运行
     *
     * @param context
     * @return
     */
    public static boolean isOnMainProcess(Context context) {
        final String pkgName = context.getPackageName();
        final String mainProcessName = pkgName + ":" + pkgName;
        String processName = getProcessNameBy(context);
        return mainProcessName.equals(processName);
    }

    public static String getProcessNameBy(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null) {
            for (ActivityManager.RunningAppProcessInfo procInfo : procInfos) {
                if (pid == procInfo.pid) {
                    return context.getPackageName() + ":" + procInfo.processName;
                }
            }
        }
        return null;
    }
}
