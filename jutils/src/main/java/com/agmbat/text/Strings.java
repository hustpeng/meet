/**
 * Copyright (C) 2016 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 *
 * jutils
 *
 * @author mayimchen
 * @since 2017-04-22
 */
package com.agmbat.text;

/**
 * 一些常用的字符串
 */
public class Strings {

    /**
     * 返回四个空格
     * 
     * @return
     */
    public static String tab() {
        return "    ";
    }

    /**
     * 返回对应的tab个数
     * 
     * @param count
     * @return
     */
    public static String tab(int count) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            builder.append(tab());
        }
        return builder.toString();
    }

    /**
     * 将给定的字符串按行添加tab键
     * 
     * @param count
     * @param text
     * @return
     */
    public static String tab(int count, String text) {
        StringBuilder builder = new StringBuilder();
        for (String line : text.split("\n")) {
            builder.append(tab(count)).append(line).append("\n");
        }
        // 去掉最后一个换行
        int length = builder.length();
        if (length > 0) {
            builder.delete(length - 1, length);
        }
        return builder.toString();
    }

    /**
     * 新起一行
     * 
     * @return
     */
    public static String newLine() {
        return "\n";
    }

    /**
     * 将所给的文本使用c++注释起来
     *
     * @param text
     * @return
     */
    public static String comment(String text) {
        if (StringUtils.isEmpty(text)) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append("/**\n");
        for (String line : text.trim().split("\n")) {
            builder.append(" * ").append(line).append("\n");
        }
        builder.append(" */\n");
        return builder.toString();
    }
}