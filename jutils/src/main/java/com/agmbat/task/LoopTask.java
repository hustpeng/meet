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
 * 循环任务
 */
public abstract class LoopTask implements Runnable {

    private volatile boolean mValue = false;

    @Override
    public void run() {
        while (true) {
            runAction();
            if (mValue) {
                return;
            }
        }
    }

    /**
     * 执行操作
     */
    public abstract void runAction();


    public void setFinished(boolean value) {
        mValue = value;
    }
}
