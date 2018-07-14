package com.agmbat.debug;

/**
 * 线程信息
 */
public class ThreadInfo {

    private long mId;
    private String mName;
    private int mPriority;

    public static ThreadInfo get() {
        Thread thread = Thread.currentThread();
        return new ThreadInfo(thread);
    }

    private ThreadInfo(Thread thread) {
        mId = thread.getId();
        mName = thread.getName();
        mPriority = thread.getPriority();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[Thread: id=").append(mId);
        builder.append(", name=").append(mName);
        builder.append(", priority=").append(mPriority);
        builder.append("]");
        return builder.toString();
    }
}