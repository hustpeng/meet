package com.agmbat.imsdk.util;

import android.util.Log;

/**
 * Created by bshao on 1/13/17.
 */

public class VLog {
    private static boolean isDebug = true;
    private static String sTag = "VLog";

    public static void setDebug(boolean debug) {
        isDebug = debug;
    }

    public static boolean isDebug() {
        return isDebug;
    }

    public static String getTag() {
        return sTag;
    }

    public static void setTag(String tag) {
        sTag = tag;
    }

    public static void v(String format, Object... args) {
        if (isDebug) {
            Log.v(sTag, buildMsg(String.format(format, args)));
        }
    }

    public static void v(String msg) {
        if (isDebug) {
            Log.v(sTag, buildMsg(msg));
        }
    }

    public static void verbose(String tag, String format, Object... args) {
        if (isDebug) {
            Log.v(tag, buildMsg(String.format(format, args)));
        }
    }


    public static void verbose(String tag, String msg) {
        if (isDebug) {
            Log.v(tag, buildMsg(msg));
        }
    }

    public static void d(String format, Object... args) {
        if (isDebug) {
            Log.d(sTag, buildMsg(String.format(format, args)));
        }
    }

    public static void d(String msg) {
        if (isDebug) {
            Log.d(sTag, buildMsg(msg));
        }
    }

    public static void debug(String tag, String format, Object... args) {
        if (isDebug) {
            Log.d(tag, buildMsg(String.format(format, args)));
        }
    }

    public static void debug(String tag, String msg) {
        if (isDebug) {
            Log.d(tag, buildMsg(msg));
        }
    }

    public static void i(String format, Object... args) {
        if (isDebug) {
            Log.i(sTag, buildMsg(String.format(format, args)));
        }
    }

    public static void i(String msg) {
        if (isDebug) {
            Log.i(sTag, buildMsg(msg));
        }
    }

    public static void info(String tag, String format, Object... args) {
        if (isDebug) {
            Log.i(tag, buildMsg(String.format(format, args)));
        }
    }

    public static void info(String tag, String msg) {
        if (isDebug) {
            Log.i(tag, buildMsg(msg));
        }
    }

    public static void w(String format, Object... args) {
        if (isDebug) {
            Log.w(sTag, buildMsg(String.format(format, args)));
        }
    }

    public static void w(String msg) {
        if (isDebug) {
            Log.w(sTag, buildMsg(msg));
        }
    }

    public static void warn(String tag, String format, Object... args) {
        if (isDebug) {
            Log.w(tag, buildMsg(String.format(format, args)));
        }
    }

    public static void warn(String tag, String msg) {
        if (isDebug) {
            Log.w(tag, buildMsg(msg));
        }
    }

    public static void w(Throwable throwable, String format, Object... args) {
        if (isDebug) {
            Log.w(sTag, buildMsg(String.format(format, args)), throwable);
        }
    }

    public static void w(Throwable throwable, String msg) {
        if (isDebug) {
            Log.w(sTag, buildMsg(msg), throwable);
        }
    }

    public static void warn(String tag, Throwable throwable, String format, Object... args) {
        if (isDebug) {
            Log.w(tag, buildMsg(String.format(format, args)), throwable);
        }
    }

    public static void warn(String tag, Throwable throwable, String msg) {
        if (isDebug) {
            Log.w(tag, buildMsg(msg), throwable);
        }
    }

    public static void e(String format, Object... args) {
        if (isDebug) {
            Log.e(sTag, buildMsg(String.format(format, args)));
        }
    }

    public static void e(String msg) {
        if (isDebug) {
            Log.e(sTag, buildMsg(msg));
        }
    }

    public static void error(String tag, String format, Object... args) {
        if (isDebug) {
            Log.e(tag, buildMsg(String.format(format, args)));
        }
    }

    public static void error(String tag, String msg) {
        if (isDebug) {
            Log.e(tag, buildMsg(msg));
        }
    }

    public static void e(Throwable throwable, String format, Object... args) {
        if (isDebug) {
            Log.e(sTag, buildMsg(String.format(format, args)), throwable);
        }
    }

    public static void e(Throwable throwable, String msg) {
        if (isDebug) {
            Log.e(sTag, buildMsg(msg), throwable);
        }
    }

    public static void error(String tag, Throwable throwable, String format, Object... args) {
        if (isDebug) {
            Log.e(tag, buildMsg(String.format(format, args)), throwable);
        }
    }

    public static void error(String tag, Throwable throwable, String msg) {
        if (isDebug) {
            Log.e(tag, buildMsg(msg), throwable);
        }
    }

    public static void e(Throwable e) {
        if (isDebug) {
            Log.e(sTag, buildMsg(getStackTraceString(e)));
        }
    }


    public static String getStackTraceString(Throwable tr) {
        return Log.getStackTraceString(tr);
    }

    private static String buildMsg(String msg) {
        StringBuilder buffer = new StringBuilder();
        final StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];
        buffer.append("[(");
        buffer.append(stackTraceElement.getFileName());
        buffer.append(":");
        buffer.append(stackTraceElement.getLineNumber());
        buffer.append(")");
//        buffer.append("#");
//        buffer.append(stackTraceElement.getMethodName());
//        buffer.append(" -> ");
//        buffer.append(Thread.currentThread().getName());
        buffer.append("] ");
        buffer.append(msg);
        return buffer.toString();
    }
}
