package com.agmbat.utils;

import java.text.DecimalFormat;

/**
 * 控制台字符型进度条。
 */
public class ConsoleProgressBar {


    /**
     * 进度条起始值
     */
    private long minimum = 0;

    /**
     * 进度条最大值
     */
    private long maximum = 100;

    /**
     * 进度条长度
     */
    private long barLen = 100;

    /**
     * 用于进度条显示的字符
     */
    private char showChar = '=';

    /**
     * 百分比格式化
     */
    private DecimalFormat formatter = new DecimalFormat("#.##%");


    private ConsoleLinePrinter printer;

    /**
     * 使用系统标准输出，显示字符进度条及其百分比。
     */
    public ConsoleProgressBar() {
        this(0, 100);
    }

    /**
     * 使用系统标准输出，显示字符进度条及其百分比。
     *
     * @param minimum 进度条起始值
     * @param maximum 进度条最大值
     */
    public ConsoleProgressBar(long minimum, long maximum) {
        this(minimum, maximum, 100);
    }

    /**
     * 使用系统标准输出，显示字符进度条及其百分比。
     *
     * @param minimum 进度条起始值
     * @param maximum 进度条最大值
     * @param barLen  进度条长度
     */
    public ConsoleProgressBar(long minimum, long maximum, long barLen) {
        this(minimum, maximum, barLen, '=');
    }

    /**
     * 使用系统标准输出，显示字符进度条及其百分比。
     *
     * @param minimum  进度条起始值
     * @param maximum  进度条最大值
     * @param barLen   进度条长度
     * @param showChar 用于进度条显示的字符
     */
    public ConsoleProgressBar(long minimum, long maximum, long barLen, char showChar) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.barLen = barLen;
        this.showChar = showChar;
        printer = new ConsoleLinePrinter();
    }

    /**
     * 构造字符
     *
     * @param ch
     * @param count
     */
    private static String buildChar(char ch, int count) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            builder.append(ch);
        }
        return builder.toString();
    }

    public static void main(String[] args) throws InterruptedException {
        ConsoleProgressBar cpb = new ConsoleProgressBar();
        for (int i = 1; i <= 100; i++) {
            cpb.show(i);
            Thread.sleep(100);
        }
    }

    /**
     * 显示进度条。
     *
     * @param value 当前进度。进度必须大于或等于起始点且小于等于结束点（start <= current <= end）。
     */
    public void show(long value) {
        if (value < minimum || value > maximum) {
            return;
        }
        // 如果最后一个,则必须强制打印
        boolean waitForPrint = value == maximum;

        // 打印进度
        float rate = (float) (value * 1.0 / maximum);
        int len = (int) (rate * barLen);
        String text = buildChar(showChar, len);
        printer.print(text + ' ' + formatter.format(rate), waitForPrint);

        if (value == maximum) {
            System.out.print('\n');
        }
    }

}