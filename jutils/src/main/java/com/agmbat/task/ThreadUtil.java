/**
 * Copyright (C) 2017 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 * <p>
 * jutils
 *
 * @author mayimchen
 * @since 2017-07-23
 */
package com.agmbat.task;

import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 线程相关工具类
 */
public class ThreadUtil {

    /**
     * sleep,忽略异常
     *
     * @param time
     */
    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 等待,忽略异常
     *
     * @param object
     */
    public static void wait(Object object) {
        try {
            object.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static boolean handleInterrruptedException(Throwable e) {
        // A helper to deal with the interrupt exception
        // If an interrupt detected, we will setup the bit again.
        if (e instanceof InterruptedIOException || e instanceof InterruptedException) {
            Thread.currentThread().interrupt();
            return true;
        }
        return false;
    }

    /**
     * 获取异常信息的堆栈跟踪信息。
     *
     * @param throwable 要解析的异常信息。
     * @return 该异常信息的堆栈跟踪信息。
     */
    public static final String getThrowableStackTrace(final Throwable throwable) {
        final StringWriter sWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(sWriter);
        throwable.printStackTrace(printWriter);
        printWriter.flush();
        printWriter.close();
        final String message = sWriter.toString();
        return message;
    }


    /**
     * 启动循环任务
     *
     * @param task
     */
    public static void startLoopTask(LoopTask task) {
        new Thread(task).start();
    }

    /**
     * 循环执行并且等待结果返回,如果method返回null, 则循环执行
     *
     * @param method
     * @param <T>
     * @return
     */
    public static <T> T loopRunAndWait(final Method<T> method) {
        return loopRunAndWait(Long.MAX_VALUE, method);
    }

    /**
     * 循环执行并且等待
     *
     * @param waitTime
     * @param method
     * @param <T>
     * @return
     */
    public static <T> T loopRunAndWait(long waitTime, final Method<T> method) {
        final Waitable waiter = new TimeWaitable(waitTime);
        final SyncResult<T> result = new SyncResult<T>();
        new LoopTask() {
            @Override
            public void runAction() {
                if (waiter.canWait()) {
                    T data = method.call();
                    if (data != null) {
                        result.set(data);
                        setFinished(true);
                    }
                } else {
                    setFinished(true);
                }
            }
        }.run();
        return result.get();
    }
}