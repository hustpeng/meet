package com.agmbat.meetyou.search;

import android.content.Context;
import android.content.Intent;

import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.asmack.roster.ContactInfo;

/**
 * 查看用户信息辅助类
 */
public class ViewUserHelper {

    public static final String KEY_TYPE = "type";

    public static final String KEY_USER_INFO = "userInfo";

    /**
     * 从联系页面进入, 查看联系人信息
     */
    public static final int TYPE_CONTACTS = 1;

    /**
     * 陌生人页面进入
     */
    public static final int TYPE_STRANGER = 2;

    /**
     * 申请者信息
     */
    public static final int TYPE_VERIFY = 3;

    /**
     * 查看用户详情
     *
     * @param context
     * @param contactInfo
     */
    public static void viewContactInfoMore(Context context, ContactInfo contactInfo) {
        XMPPManager.getInstance().getRosterManager().addContactToMemCache(contactInfo);
        Intent intent = new Intent(context, MoreUserInfoActivity.class);
        intent.putExtra(KEY_USER_INFO, contactInfo.getBareJid());
        context.startActivity(intent);
    }

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

    /**
     * 打开用户信息界面
     *
     * @param context
     * @param contactInfo
     * @param type
     */
    private static void openDetail(Context context, ContactInfo contactInfo, int type) {
        XMPPManager.getInstance().getRosterManager().addContactToMemCache(contactInfo);
        Intent intent = new Intent(context, UserInfoActivity.class);
        intent.putExtra(KEY_USER_INFO, contactInfo.getBareJid());
        intent.putExtra(KEY_TYPE, type);
        context.startActivity(intent);
    }

    public static ContactInfo getContactInfoFromIntent(Intent intent) {
        String jid = intent.getStringExtra(KEY_USER_INFO);
        ContactInfo contactInfo = XMPPManager.getInstance().getRosterManager().getContactFromMemCache(jid);
        return contactInfo;
    }

    /**
     * 获取查看用户信息界面的处理
     *
     * @param intent
     * @return
     */
    public static BusinessHandler getBusinessHandler(Intent intent) {
        ContactInfo contactInfo = getContactInfoFromIntent(intent);
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

    /**
     * 设置Intent中的参数为联系人
     *
     * @param intent
     */
    public static void setContactType(Intent intent) {
        intent.putExtra(KEY_TYPE, TYPE_CONTACTS);
    }
}
