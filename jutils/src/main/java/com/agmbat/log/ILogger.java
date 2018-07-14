package com.agmbat.log;

/**
 * 打印日志的接口
 */
public interface ILogger {

    /**
     * Prints a log message.
     *
     * @param priority
     * @param tag
     * @param msg
     */
    public void println(int priority, String tag, String msg);
}
