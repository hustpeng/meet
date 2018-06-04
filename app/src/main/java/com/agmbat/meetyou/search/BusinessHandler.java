package com.agmbat.meetyou.search;

import android.content.Context;
import android.view.View;

import com.agmbat.imsdk.asmack.roster.ContactInfo;
import com.agmbat.menu.PopupMenu;

/**
 * 查看用户信息界面 , 业务处理
 */
public abstract class BusinessHandler {

    private ContactInfo mContactInfo;

    public BusinessHandler(ContactInfo contactInfo) {
        mContactInfo = contactInfo;
    }

    public ContactInfo getContactInfo() {
        return mContactInfo;
    }

    /**
     * 配置view显示
     *
     * @param view
     */
    public abstract void setupViews(View view);

    /**
     * 准备menu显示
     *
     * @param popupMenu
     */
    public void onPrepareMoreMenu(Context context, PopupMenu popupMenu, ContactInfo contactInfo) {
    }
}
