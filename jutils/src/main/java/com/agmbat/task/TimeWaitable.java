/**
 * Copyright (C) 2017 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 * <p>
 * jutils
 *
 * @author mayimchen
 * @since 2017-07-23
 */
package com.agmbat.task;

/**
 * 超时等待器
 */
public class TimeWaitable implements Waitable {

    /**
     * 等待总时间
     */
    private final long mTime;

    /**
     * 已经走过的时间
     */
    private long mPassTime = 0;

    /**
     * 间隔时间
     */
    private final long mInterval = 100;

    public TimeWaitable(long time) {
        mTime = time;
    }

    @Override
    public boolean canWait() {
        if (mPassTime > mTime) {
            return false;
        }
        ThreadUtil.sleep(mInterval);
        mPassTime += mInterval;
        return true;
    }
}

