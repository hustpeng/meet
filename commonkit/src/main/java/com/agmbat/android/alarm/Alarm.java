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

import java.util.Random;

/**
 * 定义闹钟抽象类，通过AlarmManager调用run方法开始运行闹钟
 */
public abstract class Alarm {

    private static final Random RANDOM = new Random();

    protected int mRequestCode;

    protected Alarm() {
        mRequestCode = RANDOM.nextInt();
    }

    protected String getId() {
        return String.valueOf(mRequestCode);
    }

    protected int getRequestCode() {
        return mRequestCode;
    }

    public abstract long getInterval();

    /**
     * 执行alarm的逻辑， 由AlarmManager调用
     */
    public abstract void run();

    /**
     * 当时间到点时，回调给用户，让用户处理事情
     */
    protected abstract void onAlarm();
}