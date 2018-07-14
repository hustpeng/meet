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

import java.util.Calendar;

/**
 * 每天定时闹钟
 */
public abstract class EveryDayAlarm extends Alarm {

    private static final long MINUTE = 60 * 1000;

    private final int mHour;
    private final int mMinute;
    private final int mSecond;

    private long mInterval;

    public EveryDayAlarm(int hour, int minute, int second) {
        mHour = hour;
        mMinute = minute;
        mSecond = second;
    }

    @Override
    public long getInterval() {
        return mInterval;
    }

    @Override
    public void run() {
        Calendar alarmTime = Calendar.getInstance();
        alarmTime.set(Calendar.HOUR_OF_DAY, mHour);
        alarmTime.set(Calendar.MINUTE, mMinute);
        alarmTime.set(Calendar.SECOND, mSecond);
        alarmTime.set(Calendar.MILLISECOND, 0);

        Calendar current = Calendar.getInstance();
        if (alarmTime.before(current)) {
            // 如果闹钟在这之前，说明闹钟时间已过，但是在1分钟内，则开始做闹钟的事，否则设置下次闹钟
            long pass = current.getTimeInMillis() - alarmTime.getTimeInMillis();
            if (pass < 1 * MINUTE) {
                // do something
                onAlarm();
            }
            // 设置每天的闹钟
            alarmTime.add(Calendar.DAY_OF_YEAR, 1);
        } // else 闹钟还没到，则设置下次的闹钟时间
        mInterval = alarmTime.getTimeInMillis() - System.currentTimeMillis();
    }

}
