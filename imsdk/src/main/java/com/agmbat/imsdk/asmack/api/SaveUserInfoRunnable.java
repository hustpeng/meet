package com.agmbat.imsdk.asmack.api;

import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.user.LoginUser;

import org.jivesoftware.smackx.vcard.VCardListener;
import org.jivesoftware.smackx.vcard.VCardManager;
import org.jivesoftware.smackx.vcard.VCardObject;
import org.jivesoftware.smackx.vcardextend.VCardExtendListener;
import org.jivesoftware.smackx.vcardextend.VCardExtendManager;
import org.jivesoftware.smackx.vcardextend.VCardExtendObject;

/**
 * 从服务端保存用户信息, 只能保存登录用户信息的信息
 */
public class SaveUserInfoRunnable implements Runnable {

    /**
     * 保存用户信息
     */
    private final LoginUser mLoginUser;
    private final OnSaveUserInfoListener listener;

    public SaveUserInfoRunnable(LoginUser contactInfo, OnSaveUserInfoListener listener) {
        mLoginUser = contactInfo;
        this.listener = listener;
    }

    @Override
    public void run() {
        saveVCardObject();
        saveVCardExtendObject();
        if (listener != null) {
            listener.onSaveUserInfo(mLoginUser);
        }
    }

    private void saveVCardExtendObject() {
        final VCardExtendManager vCardExtendManager = XMPPManager.getInstance().getvCardExtendManager();
        VCardExtendListener mVCardExtendListener = new VCardExtendListener() {

            @Override
            public void notifySetMyVCardExtendResult(boolean success) {
                vCardExtendManager.removeListener(this);
                synchronized (mLoginUser) {
                    try {
                        mLoginUser.notifyAll();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void notifyFetchVCardExtendResult(String jid, VCardExtendObject vcardExtend) {
            }
        };

        vCardExtendManager.addListener(mVCardExtendListener);
        vCardExtendManager.setMyVCardExtend(mLoginUser.getVCardExtendObject());

        synchronized (mLoginUser) {
            try {
                mLoginUser.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveVCardObject() {
        final VCardManager vCardManager = XMPPManager.getInstance().getvCardManager();
        VCardListener vCardListener = new VCardListener() {
            @Override
            public void notifyFetchVCardResult(String jid, VCardObject vcard) {
            }

            @Override
            public void notifySetMyVCardResult(boolean success) {
                vCardManager.removeListener(this);
                synchronized (mLoginUser) {
                    try {
                        mLoginUser.notifyAll();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        vCardManager.addListener(vCardListener);
        vCardManager.setMyVCard(mLoginUser.getVCardObject());

        synchronized (mLoginUser) {
            try {
                mLoginUser.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}