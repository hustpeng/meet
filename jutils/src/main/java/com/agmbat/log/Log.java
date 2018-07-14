package com.agmbat.log;

import com.agmbat.utils.Platform;
import com.agmbat.text.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Log工具
 */
public class Log {

    /**
     * Priority constant for enable all loggings.
     */
    public static final int ALL = -1;

    /**
     * Priority constant for {@link #println(int, String, String)} or {@link #setFilterLevel(int)} methods; use Log.p.
     */
    public static final int PROFILE = 1;

    /**
     * Priority constant for {@link #println(int, String, String)} or {@link #setFilterLevel(int)} methods; use Log.v.
     */
    public static final int VERBOSE = 2;

    /**
     * Priority constant for the {@link #println(int, String, String)} or {@link #setFilterLevel(int)} method; use
     * Log.d.
     */
    public static final int DEBUG = 3;

    /**
     * Priority constant for the {@link #println(int, String, String)} or {@link #setFilterLevel(int)} method; use
     * Log.i.
     */
    public static final int INFO = 4;

    /**
     * Priority constant for the {@link #println(int, String, String)} or {@link #setFilterLevel(int)} method; use
     * Log.w.
     */
    public static final int WARN = 5;

    /**
     * Priority constant for the {@link #println(int, String, String)} or {@link #setFilterLevel(int)} method; use
     * Log.e.
     */
    public static final int ERROR = 6;

    /**
     * Priority constant for the {@link #println(int, String, String)} or {@link #setFilterLevel(int)} method.
     */
    public static final int ASSERT = 7;

    /**
     * Priority constant for disable all loggings.
     */
    public static final int NONE = Integer.MAX_VALUE;

    /**
     * Filter level of logs. Only levels greater or equals this level will be output.
     */
    private static int sFilterLevel = ALL;

    /**
     * The logger for print loggings
     */
    private static ILogger sLogger;

    private Log() {
    }

    public static void setLogger(ILogger logger) {
        sLogger = logger;
    }

    /**
     * Sets the filter level of logs.
     *
     * @param level The filter level.
     */
    public static void setFilterLevel(int level) {
        sFilterLevel = level;
    }

    /**
     * Gets the filter level of logs.
     *
     * @return Current filter level.
     */
    public static int getFilterLevel() {
        return sFilterLevel;
    }

    /**
     * Send a {@link #VERBOSE} log message.
     *
     * @param msg The message you would like logged.
     */
    public static void v(String msg) {
        println(VERBOSE, null, msg);
    }

    /**
     * Send a {@link #VERBOSE} log message.
     *
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the
     *            log call occurs.
     * @param msg The message you would like logged.
     */
    public static void v(String tag, String msg) {
        println(VERBOSE, tag, msg);
    }

    /**
     * Send a {@link #VERBOSE} log message.
     *
     * @param tag    Used to identify the source of a log message. It usually identifies the class or activity where the
     *               log call occurs.
     * @param format The format of the message you would like logged.
     * @param args   The arguments used to format the message.
     */
    public static void v(String tag, String format, Object... args) {
        String msg = String.format(format, args);
        println(VERBOSE, tag, msg);
    }

    /**
     * Send a {@link #VERBOSE} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the
     *            log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void v(String tag, String msg, Throwable tr) {
        println(VERBOSE, tag, msg + '\n' + getStackTraceString(tr));
    }

    /**
     * Send a {@link #DEBUG} log message.
     *
     * @param msg The message you would like logged.
     */

    public static void d(String msg) {
        println(DEBUG, null, msg);
    }

    /**
     * Send a {@link #DEBUG} log message.
     *
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the
     *            log call occurs.
     * @param msg The message you would like logged.
     */
    public static void d(String tag, String msg) {
        println(DEBUG, tag, msg);
    }

    /**
     * Send a {@link #DEBUG} log message.
     *
     * @param tag    Used to identify the source of a log message. It usually identifies the class or activity where the
     *               log call occurs.
     * @param format The format of the message you would like logged.
     * @param args   The arguments used to format the message.
     */
    public static void d(String tag, String format, Object... args) {
        final String msg = String.format(format, args);
        println(DEBUG, tag, msg);
    }

    /**
     * Send a {@link #DEBUG} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the
     *            log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void d(String tag, String msg, Throwable tr) {
        println(DEBUG, tag, msg + '\n' + getStackTraceString(tr));
    }

    /**
     * Send an {@link #INFO} log message.
     *
     * @param msg The message you would like logged.
     */

    public static void i(String msg) {
        println(INFO, null, msg);
    }

    /**
     * Send an {@link #INFO} log message.
     *
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the
     *            log call occurs.
     * @param msg The message you would like logged.
     */
    public static void i(String tag, String msg) {
        println(INFO, tag, msg);
    }

    /**
     * Send an {@link #INFO} log message.
     *
     * @param tag    Used to identify the source of a log message. It usually identifies the class or activity where the
     *               log call occurs.
     * @param format The format of the message you would like logged.
     * @param args   The arguments used to format the message.
     */
    public static void i(String tag, String format, Object... args) {
        final String msg = String.format(format, args);
        println(INFO, tag, msg);
    }

    /**
     * Send a {@link #INFO} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the
     *            log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void i(String tag, String msg, Throwable tr) {
        println(INFO, tag, msg + '\n' + getStackTraceString(tr));
    }

    /**
     * Send a {@link #WARN} log message.
     *
     * @param msg The message you would like logged.
     */

    public static void w(String msg) {
        println(WARN, null, msg);
    }

    /**
     * Send a {@link #WARN} log message.
     *
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the
     *            log call occurs.
     * @param msg The message you would like logged.
     */
    public static void w(String tag, String msg) {
        println(WARN, tag, msg);
    }

    /**
     * Send a {@link #WARN} log message.
     *
     * @param tag    Used to identify the source of a log message. It usually identifies the class or activity where the
     *               log call occurs.
     * @param format The format of the message you would like logged.
     * @param args   The arguments used to format the message.
     */
    public static void w(String tag, String format, Object... args) {
        String msg = String.format(format, args);
        println(WARN, tag, msg);
    }

    /**
     * Send a {@link #WARN} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the
     *            log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void w(String tag, String msg, Throwable tr) {
        println(WARN, tag, msg + '\n' + getStackTraceString(tr));
    }

    /**
     * Send a {@link #WARN} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the
     *            log call occurs.
     * @param tr  An exception to log
     */
    public static void w(String tag, Throwable tr) {
        println(WARN, tag, getStackTraceString(tr));
    }

    public static void w(Throwable tr) {
        println(WARN, null, getStackTraceString(tr));
    }

    /**
     * Send an {@link #ERROR} log message.
     *
     * @param tag    Used to identify the source of a log message. It usually identifies the class or activity where the
     *               log call occurs.
     * @param format The format of the message you would like logged.
     * @param args   The arguments used to format the message.
     */
    public static void e(String tag, String format, Object... args) {
        String msg = String.format(format, args);
        println(ERROR, tag, msg);
    }

    /**
     * Send an {@link #ERROR} log message.
     *
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the
     *            log call occurs.
     * @param msg The message you would like logged.
     */
    public static void e(String tag, String msg) {
        println(ERROR, tag, msg);
    }

    /**
     * Send a {@link #ERROR} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the
     *            log call occurs.
     * @param tr  An exception to log
     */
    public static void e(String tag, Throwable tr) {
        println(ERROR, tag, getStackTraceString(tr));
    }

    /**
     * Send a {@link #ERROR} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the
     *            log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void e(String tag, String msg, Throwable tr) {
        println(ERROR, tag, msg + '\n' + getStackTraceString(tr));
    }

    /**
     * Send an {@link #ERROR} log message.
     *
     * @param msg The message you would like logged.
     */

    public static void e(String msg) {
        println(ERROR, null, msg);
    }

    /**
     * Send an {@link #ERROR} log message.
     *
     * @param tr An exception to log
     */
    public static void e(Throwable tr) {
        println(ERROR, null, getStackTraceString(tr));
    }

    /**
     * Handy function to get a loggable stack trace from a Throwable
     *
     * @param tr An exception to log
     */
    private static String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * Low-level logging call.
     *
     * @param priority The priority/type of this log message
     * @param tag      Used to identify the source of a log message. It usually identifies the class or activity where the
     *                 log call occurs.
     * @param msg      The message you would like logged.
     * @return The number of bytes written.
     */
    static void println(int priority, String tag, String msg) {
        if (priority < sFilterLevel) {
            return;
        }
        if (sLogger != null) {
            // 如果tag为空,则解析成当前调用的类名
            if (StringUtils.isEmpty(tag)) {
                tag = genTag();
            }
            sLogger.println(priority, tag, msg);
        }
    }

    /**
     * 获取Tag信息,通过调用栈获取类名
     *
     * @return
     */
    private static String genTag() {
        StackTraceElement[] elements = new Throwable().getStackTrace();
        final String currentClsName = Log.class.getName();
        StackTraceElement ele = null;
        for (StackTraceElement e : elements) {
            if (!currentClsName.equals(e.getClassName())) {
                ele = e;
                break;
            }
        }
        try {
            Class<?> cls = Class.forName(ele.getClassName());
            return cls.getSimpleName();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }


    static {
        // 当前平台
        boolean isAndroid = Platform.isAndroid();
        if (isAndroid) {
            sLogger = new AndroidLogger();
        } else {
            sLogger = new JavaLogger();
        }
    }
}
