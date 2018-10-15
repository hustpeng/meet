/**
 * Copyright (C) 2016 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 * <p>
 * jutils
 *
 * @author mayimchen
 * @since 2016-10-07
 */
package com.agmbat.text;

import java.util.regex.Pattern;

public class PhoneNumberUtil {

    /**
     * 中国移动：China Mobile
     * 134[0-8],135,136,137,138,139,150,151,157,158,159,182,187,188
     */
    //private static Pattern CM = Pattern.compile("^1(34[0-8]|(3[5-9]|5[017-9]|8[278])\\d)\\d{7}$");
    /**
     * 中国联通：China Unicom
     * 130,131,132,152,155,156,185,186
     */
    //private static Pattern CU = Pattern.compile("^1(3[0-2]|5[256]|8[56])\\d{8}$");

    /**
     * 中国电信：China Telecom
     * 133,1349,153,180,189
     */
    //private static Pattern CT = Pattern.compile("^1((33|53|8[09])[0-9]|349)\\d{7}$");

    /**
     * 手机号码
     * 移动：134[0-8],135,136,137,138,139,150,151,157,158,159,182,187,188
     * 联通：130,131,132,152,155,156,185,186
     * 电信：133,1349,153,180,189
     */
    private static final Pattern MOBILE = Pattern.compile("^1(3[0-9]|5[0-35-9]|8[025-9])\\d{8}$");

    /**
     * 判断是否为手机号码
     *
     * @param phoneNumber
     * @return
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (StringUtils.isEmpty(phoneNumber)) {
            return false;
        }
        if (MOBILE.matcher(phoneNumber).matches()) {
            return true;
        }
        return false;
    }

    public static String normalizePhoneNumber(String phoneNumber) {
        if (StringUtils.isEmpty(phoneNumber)) {
            return phoneNumber;
        }
        return phoneNumber.replaceAll("[- ()]", "");
    }

    public static String formatPhoneNumber(String number) {
        if (StringUtils.isEmpty(number)) {
            return "";
        }
        if (number != null && number.startsWith("+86")) {
            number = number.substring(3);
        }
        number = number.replace("-", "").replace(" ", "");
        return number;
    }

}
