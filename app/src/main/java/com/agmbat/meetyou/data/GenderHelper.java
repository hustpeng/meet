package com.agmbat.meetyou.data;

import com.agmbat.meetyou.R;

/**
 * 性别辅助类
 */
public class GenderHelper {

    /**
     * 0表示未设置
     */
    public static final int GENDER_UNKNOWN = 0;

    /**
     * 1表示男
     */
    public static final int GENDER_MALE = 1;

    /**
     * 2表示女
     */
    public static final int GENDER_FEMALE = 2;

    /**
     * 3表示保密
     */
    public static final int GENDER_SECRET = 3;

    /**
     * 获取性别名称文本
     *
     * @param gender
     * @return
     */
    public static String getName(int gender) {
        if (gender == GENDER_UNKNOWN) {
            return "未设置";
        } else if (gender == GENDER_MALE) {
            return "男";
        } else if (gender == GENDER_FEMALE) {
            return "女";
        } else if (gender == GENDER_SECRET) {
            return "保密";
        }
        return "";
    }

    /**
     * 获取标示资源
     *
     * @param gender
     * @return
     */
    public static int getIconRes(int gender) {
        if (gender == GENDER_MALE) {
            return R.drawable.im_ic_gender_male;
        } else if (gender == GENDER_FEMALE) {
            return R.drawable.im_ic_gender_female;
        }
        return 0;
    }
}
