package com.agmbat.meetyou.helper;

import android.text.TextUtils;

import com.agmbat.imsdk.asmack.roster.ContactInfo;
import com.agmbat.meetyou.R;

/**
 * 用户信息显示处理
 */
public class UserInfoDisplay {

    /**
     * 获取显示的用户名
     * 查看对方的资料显示的遇缘号直接是手机号码，需要把手机号码的八，九，十三个数字用^-^代替，以免涉及泄漏手机号码，
     * 例如号码为13012345678则显示为1301234^-^8
     *
     * @param username
     * @return
     */
    public static String getDisplayUserName(String username) {
        if (TextUtils.isEmpty(username)) {
            return "";
        }
        if (username.length() != 11) {
            // 如果不是11位数, 就不处理
            return username;
        }
        return username.substring(0, 7) + "^-^" + username.substring(10);
    }

    /**
     * 获取用户认证状态图标
     * @param authStatus
     * @return
     */
    public static int getAuthStatusIcon(int authStatus) {
        int iconRes = 0;
        if (authStatus == ContactInfo.AUTH_STATE_AUTHENTICATED) {
            iconRes = R.drawable.ic_v;
        } else if (authStatus == ContactInfo.AUTH_STATE_SENIOR) {
            iconRes = R.drawable.ic_crown;
        }
        return iconRes;
    }
}
