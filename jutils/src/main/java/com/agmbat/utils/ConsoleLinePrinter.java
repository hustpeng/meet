package com.agmbat.utils;

public class ConsoleLinePrinter {

    /**
     * 最小的打印间隔时间必须为1秒,否则显示会出错
     */
    private static final long INTERVAL = 800;

    /**
     * 上次打印时间
     */
    private long printTime = 0;

    public void print(String text) {
        print(text, false);
    }

    public void print(String text, boolean wait) {
        if (!canPrint(wait)) {
            return;
        }
        System.out.print('\r' + text);
        System.out.flush();
    }

    /**
     * 是否可以打印字符,打印字符必须有一定的间隔时间, 否则会出现字符连在一起的情况
     *
     * @return
     */
    private boolean canPrint(boolean force) {
        long current = System.currentTimeMillis();
        long waitTime = current - printTime;

        if (!force) {
            if (waitTime < INTERVAL) {
                return false;
            }
        } else {
            if (waitTime < INTERVAL) {
                try {
                    Thread.sleep(INTERVAL - waitTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        printTime = current;
        return true;
    }

}
