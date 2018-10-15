package com.agmbat.log;

import com.agmbat.utils.ReflectionUtils;

import java.util.regex.Pattern;

/**
 * Android平台上打印Log
 */
public class AndroidLogger implements ILogger {

    private static final Pattern NEW_LINE_PATTERN = Pattern.compile("\r|\r\n|\n");

    /**
     * Low-level logging call.
     *
     * @param priority The priority/type of this log message
     * @param tag      Used to identify the source of a log message. It usually identifies the class or activity where the
     *                 log call occurs.
     * @param msg      The message you would like logged.
     * @return The number of bytes written.
     */
    private static void printAndroidLog(int priority, String tag, String msg) {
        Class<?>[] parameterTypes = new Class[]{int.class, String.class, String.class};
        Object[] parameters = new Object[]{priority, tag, msg};
        ReflectionUtils.invokeStaticMethod("android.util.Log", "println", parameterTypes, parameters);
    }

    /**
     * 获取详细信息
     *
     * @param msg
     * @return
     */
    private static String getDetails(String msg) {
        StackTraceElement[] elements = new Throwable().getStackTrace();
        final String currentClsName = AndroidLogger.class.getName();
        StackTraceElement ele = null;
        for (StackTraceElement e : elements) {
            if (!currentClsName.equals(e.getClassName())) {
                ele = e;
                break;
            }
        }
        String fileName = ele.getFileName();
        // String className = ele.getClassName();
        String methodName = ele.getMethodName();
        int lineNumber = ele.getLineNumber();
        return String.format("(%s:%d)>%s:%s", fileName, lineNumber, methodName, msg);
    }

    @Override
    public void println(int priority, String tag, String msg) {
        if (priority == Log.DEBUG) {
            msg = getDetails(msg);
        }
        final String[] messageLines = NEW_LINE_PATTERN.split(msg);
        for (String message : messageLines) {
            printAndroidLog(priority, tag, message);
        }
    }

}
