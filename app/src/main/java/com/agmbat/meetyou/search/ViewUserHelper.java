package com.agmbat.meetyou.search;

import android.content.Context;
import android.content.Intent;

import com.agmbat.imsdk.asmack.ContactManager;
import com.agmbat.imsdk.data.ContactInfo;
import com.agmbat.imsdk.user.UserManager;

/**
 * 查看用户信息辅助类
 */
public class ViewUserHelper {

    private static final String KEY_TYPE = "type";

    private static final String KEY_USER_INFO = "userInfo";

    /**
     * 从联系页面进入, 查看联系人信息
     */
    private static final int TYPE_CONTACTS = 1;

    /**
     * 陌生人页面进入
     */
    private static final int TYPE_STRANGER = 2;

    /**
     * 申请者信息
     */
    private static final int TYPE_VERIFY = 3;

    /**
     * 查看联系人详情
     *
     * @param context
     * @param contactInfo
     */
    public static void openContactDetail(Context context, ContactInfo contactInfo) {
        openDetail(context, contactInfo, TYPE_CONTACTS);
    }

    /**
     * 查看陌生人详情
     *
     * @param context
     * @param contactInfo
     */
    public static void openStrangerDetail(Context context, ContactInfo contactInfo) {
        openDetail(context, contactInfo, TYPE_STRANGER);
    }

    /**
     * 打开验证用户界面
     *
     * @param context
     * @param contactInfo
     */
    public static void openVerifyDetail(Context context, ContactInfo contactInfo) {
        openDetail(context, contactInfo, TYPE_VERIFY);
    }

    private static void openDetail(Context context, ContactInfo contactInfo, int type) {
        Intent intent = new Intent(context, UserInfoActivity.class);
        intent.putExtra(KEY_USER_INFO, contactInfo.getBareJid());
        intent.putExtra(KEY_TYPE, type);
        context.startActivity(intent);
    }

    /**
     * 获取查看用户信息界面的处理
     *
     * @param intent
     * @return
     */
    public static BusinessHandler getBusinessHandler(Intent intent) {
        String jid = intent.getStringExtra(KEY_USER_INFO);
        ContactInfo contactInfo = ContactManager.getContactInfo(jid);
        int type = intent.getIntExtra(KEY_TYPE, 0);
        if (type == TYPE_CONTACTS) {
            return new ContactsBusinessHandler(contactInfo);
        } else if (type == TYPE_STRANGER) {
            return new StrangerBusinessHandler(contactInfo);
        } else if (type == TYPE_VERIFY) {
            return new VerifyBusinessHandler(contactInfo);
        }
        return null;
    }
}
