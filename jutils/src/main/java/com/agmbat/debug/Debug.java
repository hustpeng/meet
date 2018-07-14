package com.agmbat.debug;

import com.agmbat.log.Log;
import com.agmbat.text.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 用于调试, 打印log或调用栈
 */
public class Debug {

    /**
     * 调用开关
     */
    private static final boolean DEBUG = true;

    /**
     * Log Tag
     */
    private static final String TAG = "DebugLog";

    /**
     * 时间格式化 MM-dd HH:mm:ss.SSS
     */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.getDefault());

    /**
     * 时间格式化为 mm:ss.SSS
     */
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

    /**
     * 打印函数调用栈
     *
     * @param object
     * @param stackMaxCount
     */
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
     * 获取调用栈字符串
     *
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

}
