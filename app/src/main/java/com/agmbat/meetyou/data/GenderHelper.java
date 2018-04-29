package com.agmbat.meetyou.data;

import com.agmbat.android.AppResources;
import com.agmbat.meetyou.R;

/**
 * 性别辅助类
 */
public class GenderHelper {

    /**
     * 0表示女
     */
    public static final int GENDER_FEMALE = 0;

    /**
     * 1表示男
     */
    public static final int GENDER_MALE = 1;

    /**
     * 男性
     *
     * @return
     */
    public static String male() {
        return AppResources.getString(R.string.gender_male);
    }

    /**
     * 女性
     *
     * @return
     */
    public static String female() {
        return AppResources.getString(R.string.gender_female);
    }

    /**
     * 获取性别名称文本
     *
     * @param gender
     * @return
     */
    public static String getName(int gender) {
        if (gender == GENDER_MALE) {
            return male();
        } else if (gender == GENDER_FEMALE) {
            return female();
        }
        return "";
    }

    /**
     * 通过name获取性别值
     *
     * @param name
     * @return
     */
    public static int getGender(String name) {
        if (male().equals(name)) {
            return GENDER_MALE;
        } else if (female().equals(name)) {
            return GENDER_FEMALE;
        }
        return GENDER_MALE;
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
