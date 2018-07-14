/**
 * Copyright (C) 2016 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 * <p>
 * jutils
 *
 * @author mayimchen
 * @since 2016-10-07
 */
package com.agmbat.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串检测处理
 */
public class StringChecker {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    private static final Pattern EMAIL_PATTERN2 = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");

    private static final Pattern NICK_NAME_PATTERN = Pattern.compile("^[_A-Za-z][_A-Za-z0-9]{2,14}$",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    private static final Pattern NICK_NAME_RESERVED_PATTERN = Pattern.compile(".*Admin.*|.*System.*",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    public static final int MAX_COMMENT_LENGTH = 1000;
    public static final int MAX_PASSWORD_LENGTH = 100;

    private static final Pattern IP_PATTERN = Pattern.compile("(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
            + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
            + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
            + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)");
    /**
     * 最小密码长度
     */
    public static final int MIN_PASSWORD_LENGTH = 6;

    /**
     * Check whether the email address is a valid email address.
     *
     * @param email the email address to check.
     * @return true if the email address is valid, false otherwise.
     */
    public static boolean isEmailValid(String email) {
        if (StringUtils.isEmpty(email)) {
            return false;
        }
        final Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

    /**
     * Determine whether a comment is valid.
     * <p/>
     * <p>
     * A valid comment must match the following rule:
     * </p>
     * <ul>
     * <li>Have a length of 1-140.</li>
     * </ul>
     *
     * @param message the content of the comment.
     * @return true if the comment is valid, false otherwise.
     */
    public static boolean isCommentValid(String message) {
        return !StringUtils.isEmpty(message) && message.length() <= MAX_COMMENT_LENGTH;
    }

    /**
     * Determine whether a nick name is valid.
     * <p/>
     * <p>
     * A valid nick name must match the following rule:
     * </p>
     * <ul>
     * <li>Have a length of 3-15.</li>
     * <li>Contains only underscore(_), letters or digits.</li>
     * <li>Digits can not be used as start of a nick name.</li>
     * <li>Must not contains any reserved words below:</li>
     * <ul>
     * <li>Admin</li>
     * <li>System</li>
     * </ul>
     * </ul>
     *
     * @param nicknName the nick name to check.
     * @return true if the nick name is valid, false otherwise.
     */
    public static boolean isNickNameValid(final String nicknName) {
        return NICK_NAME_PATTERN.matcher(nicknName).matches()
                && !NICK_NAME_RESERVED_PATTERN.matcher(nicknName).matches();
    }

    /**
     * 查找IP地址
     *
     * @param text
     * @return
     */
    public static String findIp(String text) {
        Matcher matcher = IP_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
}
