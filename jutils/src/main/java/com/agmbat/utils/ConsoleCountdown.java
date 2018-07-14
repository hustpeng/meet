package com.agmbat.utils;

import com.agmbat.task.ThreadUtil;

/**
 * 控制台字符型倒计时
 */
public class ConsoleCountdown {

    public interface Callback {
        String getText(long total, long remain);
    }

    private ConsoleLinePrinter mPrinter;
    private long mTime;
    private Callback mCallback;

    public static void main(String[] args) {
        new ConsoleCountdown(new Callback() {
            @Override
            public String getText(long total, long remain) {
                return "请等待" + String.valueOf(remain / 1000) + "秒";
            }
        }).start();
    }

    public ConsoleCountdown(Callback callback) {
        mPrinter = new ConsoleLinePrinter();
        mTime = 10000;
        mCallback = callback;
    }


    public void start() {
        final long current = System.currentTimeMillis();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    long pass = System.currentTimeMillis() - current;
                    long remain = mTime - pass;
                    if (remain > 0) {
                        mPrinter.print(mCallback.getText(mTime, remain));
                        if (remain > 1000) {
                            ThreadUtil.sleep(1000);
                        }
                    } else {
                        return;
                    }
                }
            }
        }).start();
    }

}
