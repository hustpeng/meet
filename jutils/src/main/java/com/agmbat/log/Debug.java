/**
 * Copyright (C) 2016 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 * <p>
 * jutils
 *
 * @author mayimchen
 * @since 2016-10-07
 */
package com.agmbat.log;

import com.agmbat.text.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * 用于调试
 */
public class Debug {

    /**
     * 调用开关
     */
    private static final boolean DEBUG = true;

    private static final String TAG = "Performance";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.getDefault());

    private static final SimpleDateFormat SHORT_FORMAT = new SimpleDateFormat("mm:ss.SSS", Locale.getDefault());

    /**
     * 存储方法开始时调用的相关Log信息
     */
    private static final Map<String, LogInfo> sMap = new HashMap<>();

    /**
     * 传入的base time
     */
    private static long sBaseTime = 0;

    /**
     * 重置base time
     */
    public static void resetBaseTime() {
        if (!DEBUG) {
            return;
        }
        sBaseTime = System.currentTimeMillis();
    }

    /**
     * 以Debug方式打印Log
     *
     * @param msg
     */
    public static void print(String msg) {
        if (!DEBUG) {
            return;
        }
        print(msg, System.currentTimeMillis());
    }

    /**
     * 以Debug方式打印Log
     *
     * @param msg
     * @param now
     */
    public static void print(String msg, long now) {
        if (!DEBUG) {
            return;
        }
        String time = null;
        if (sBaseTime > 0) {
            long relativeTime = now - sBaseTime;
            time = shortLogTime(relativeTime);
        } else {
            time = androidLogTime();
        }
        String logMsg = "[" + time + "]" + msg;
        Log.d(TAG, logMsg);
    }

    /**
     * 方法开始时调用
     */
    public static void onMethodStart() {
        onMethodStartOfName(null);
    }

    /**
     * 方法结束时调用
     */
    public static void onMethodEnd() {
        onMethodEndOfName(null);
    }

    /**
     * 方法开始时调用
     *
     * @param object 实例名称，用于区分多个实例
     */
    public static void onMethodStart(Object object) {
        if (!DEBUG) {
            return;
        }
        String name = (object == null) ? null : String.valueOf(object.hashCode());
        onMethodStartOfName(name);
    }

    /**
     * 方法结束时调用
     *
     * @param object 实例名称，用于区分多个实例
     */
    public static void onMethodEnd(Object object) {
        if (!DEBUG) {
            return;
        }
        String name = (object == null) ? null : String.valueOf(object.hashCode());
        onMethodEndOfName(name);
    }

    /**
     * 方法开始时调用
     *
     * @param instance 实例名称，用于区分多个实例
     */
    private static void onMethodStartOfName(String instance) {
        if (!DEBUG) {
            return;
        }
        LogInfo info = genLogInfo();
        if (info == null) {
            return;
        }
        info.mStartTime = System.currentTimeMillis();
        info.mInstanceName = instance;
        sMap.put(info.key(), info);
        info.printStartTime();
    }

    /**
     * 方法结束时调用
     *
     * @param instance 实例名称，用于区分多个实例
     */
    private static void onMethodEndOfName(String instance) {
        if (!DEBUG) {
            return;
        }
        long time = System.currentTimeMillis();
        LogInfo info = genLogInfo();
        if (info == null) {
            return;
        }
        info.mInstanceName = instance;
        LogInfo exist = sMap.remove(info.key());
        if (exist == null) {
            Log.d(TAG, "miss start method:" + info);
            return;
        }
        exist.mEndTime = time;
        exist.printEndTime();
    }

    /**
     * 创建LogInfo
     *
     * @return
     */
    private static LogInfo genLogInfo() {
        StackTraceElement[] elements = new Throwable().getStackTrace();
        final String currentClsName = Debug.class.getName();
        StackTraceElement ele = null;
        for (StackTraceElement e : elements) {
            if (!currentClsName.equals(e.getClassName())) {
                ele = e;
                break;
            }
        }
        if (ele == null) {
            return null;
        }
        String fileName = ele.getFileName();
        String className = ele.getClassName();
        String methodName = ele.getMethodName();
        LogInfo info = new LogInfo();
        info.mFileName = fileName;
        info.mClassName = className;
        info.mMethodName = methodName;
        return info;
    }

    /**
     * 格式化为android log显示时间
     *
     * @return
     */
    private static String androidLogTime() {
        return DATE_FORMAT.format(new Date());
    }

    /**
     * 格式化时间
     *
     * @param time
     * @return
     */
    private static String shortLogTime(long time) {
        return SHORT_FORMAT.format(new Date(time));
    }

    /**
     * 打印函数调用栈
     */
    public static void printStackTrace() {
        printStackTrace(null);
    }

    /**
     * 打印函数调用栈
     */
    public static void printStackTrace(Object object) {
        printStackTrace(object, 10);
    }

    public static void printStackTrace(Object object, int stackMaxCount) {
        if (!DEBUG) {
            return;
        }
        StackTraceElement[] elements = new Throwable().getStackTrace();
        final String currentClsName = Debug.class.getName();
        List<StackTraceElement> list = new ArrayList<StackTraceElement>();
        for (StackTraceElement e : elements) {
            if (!currentClsName.equals(e.getClassName())) {
                list.add(e);
            }
        }
        int count = Math.min(stackMaxCount, list.size());
        list = list.subList(0, count);
        StackTraceElement[] array = list.toArray(new StackTraceElement[count]);
        String msg = getStackTraceText(array);
        String name = (object == null) ? null : String.valueOf(object.hashCode());
        msg = "=========begin=========\n" + msg + "=========end=========\n";
        if (!StringUtils.isEmpty(name)) {
            msg = "[object:" + name + "]" + msg;
        }
        msg = ThreadInfo.get().toString() + msg;
        Log.d(TAG, msg);
    }

    /**
     * @param stack the parent stack trace to suppress duplicates from, or null if this stack trace has no parent.
     */
    private static String getStackTraceText(StackTraceElement[] stack) {
        StringBuilder builder = new StringBuilder();
        for (StackTraceElement ele : stack) {
            builder.append(ele.toString());
            builder.append("\n");
        }
        return builder.toString();
    }

    /**
     * 线程信息
     */
    private static class ThreadInfo {

        private long mId;
        private String mName;
        private int mPriority;

        private ThreadInfo(Thread thread) {
            mId = thread.getId();
            mName = thread.getName();
            mPriority = thread.getPriority();
        }

        public static ThreadInfo get() {
            Thread thread = Thread.currentThread();
            return new ThreadInfo(thread);
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

    /**
     * Log信息
     */
    private static class LogInfo {

        /**
         * 文件名
         */
        public String mFileName;

        /**
         * 类名
         */
        public String mClassName;

        /**
         * 方法名
         */
        public String mMethodName;

        /**
         * 实例名称
         */
        public String mInstanceName;

        /**
         * 方法开始时间
         */
        public long mStartTime;

        /**
         * 方法结束时间
         */
        public long mEndTime;

        /**
         * 计算方法执行时间
         *
         * @return
         */
        public long calcCost() {
            return mEndTime - mStartTime;
        }

        public String key() {
            return mFileName + ":" + mClassName + ":" + mInstanceName + ":" + mMethodName;
        }

        @Override
        public String toString() {
            return key();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o != null) {
                if (o instanceof LogInfo) {
                    LogInfo other = (LogInfo) o;
                    return Objects.equals(key(), other.key());
                }
            }
            return false;
        }

        /**
         * 打印方法开始时间
         */
        public void printStartTime() {
            StringBuilder builder = new StringBuilder();
            builder.append(getClassSimpleName()).append("::");
            builder.append(mMethodName).append("--");
            builder.append("start");
            if (!StringUtils.isEmpty(mInstanceName)) {
                builder.append(":").append(mInstanceName);
            }
            String msg = builder.toString();
            print(msg, mStartTime);
        }

        /**
         * 打印方法结束时间
         */
        public void printEndTime() {
            StringBuilder builder = new StringBuilder();
            builder.append(getClassSimpleName()).append("::");
            builder.append(mMethodName).append("--");
            builder.append("end");
            if (!StringUtils.isEmpty(mInstanceName)) {
                builder.append(":").append(mInstanceName);
            }
            builder.append(",cost:").append(calcCost());
            String msg = builder.toString();
            print(msg, mEndTime);
        }

        /**
         * 获取简单的类名
         *
         * @return
         */
        private String getClassSimpleName() {
            String name = mClassName;
            int dot = name.lastIndexOf('.');
            if (dot != -1) {
                return name.substring(dot + 1);
            }
            return name;
        }
    }

}
