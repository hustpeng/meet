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
 * 多线程运行时需要的同步Runnable
 */
public class SyncRunnable implements Runnable {

    private final Runnable mTarget;
    private boolean mComplete;

    public SyncRunnable(Runnable target) {
        mTarget = target;
    }

    @Override
    public void run() {
        mTarget.run();
        synchronized (this) {
            mComplete = true;
            notifyAll();
        }
    }

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
}
