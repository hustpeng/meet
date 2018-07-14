/**
 * Copyright (C) 2016 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 * <p>
 * jutils
 *
 * @author mayimchen
 * @since 2016-09-22
 */
package com.agmbat.task;

/**
 * 多线程运行时需要的等待运行的结果
 */
public class SyncResult<T> {

    /**
     * 执行完的结果
     */
    private T mData;

    /**
     * 执行是否完成
     */
    private boolean mComplete;

    public void waitForComplete() {
        synchronized (this) {
            while (!mComplete) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    // ignored exception
                }
            }
        }
    }

    public void notifyComplete(T data) {
        mData = data;
        synchronized (this) {
            mComplete = true;
            notifyAll();
        }
    }

    public T get() {
        return mData;
    }

    public void set(T data) {
        mData = data;
    }
}
