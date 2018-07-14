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
package com.agmbat.android.alarm;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.agmbat.android.SystemManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 封装系统闹钟服务，管理自定义的闹钟
 */
public class AlarmManager {

    private final Context mContext;
    private final android.app.AlarmManager mSysAlarm;

    private final Map<String, Alarm> mMap = new HashMap<String, Alarm>();

    private static class SingletonHolder {
        private static final AlarmManager INSTANCE = new AlarmManager();
    }

    public static AlarmManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private AlarmManager() {
        mContext = SystemManager.getContext();
        mSysAlarm = SystemManager.getAlarmManager();
        registerReceiver();
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AlarmReceiver.ACTION);
        mContext.registerReceiver(new AlarmReceiver(), filter);
    }

    public void register(Alarm alarm) {
        mMap.put(alarm.getId(), alarm);
    }

    public void unregister(Alarm alarm) {
        if (alarm != null) {
            mSysAlarm.cancel(getOperation(alarm));
            mMap.remove(alarm.getId());
        }
    }

    public Alarm getAlarm(String id) {
        return mMap.get(id);
    }

    public void scheduleAllAlarm() {
        Iterator<Alarm> alarms = mMap.values().iterator();
        while (alarms.hasNext()) {
            Alarm alarm = alarms.next();
            scheduleAlarm(alarm);
        }
    }

    public void scheduleAlarm(Alarm alarm) {
        alarm.run(); // 强制执行一次
        setNextAlarm(alarm);
    }

    public void setNextAlarm(Alarm alarm) {
        long interval = alarm.getInterval();
        if (interval > 0) {
            long time = System.currentTimeMillis() + interval;
            mSysAlarm.set(android.app.AlarmManager.RTC, time, getOperation(alarm));
        } else {
            unregister(alarm);
        }
    }

    private PendingIntent getOperation(Alarm alarm) {
        Intent intent = new Intent(AlarmReceiver.ACTION);
        intent.putExtra(AlarmReceiver.KEY_ALARM_ID, alarm.getId());
        return PendingIntent.getBroadcast(mContext, alarm.getRequestCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static class AlarmReceiver extends BroadcastReceiver {

        private static final String ACTION = "alarm_receiver";
        private static final String KEY_ALARM_ID = "alarm_id";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            String id = intent.getStringExtra(KEY_ALARM_ID);
            Alarm alarm = AlarmManager.getInstance().getAlarm(id);
            if (alarm != null) {
                AlarmManager.getInstance().scheduleAlarm(alarm);
            }
        }
    }
}
