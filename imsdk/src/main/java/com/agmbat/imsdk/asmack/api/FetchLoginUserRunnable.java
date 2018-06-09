package com.agmbat.imsdk.asmack.api;

import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.user.LoginUser;
import com.agmbat.log.Debug;

import org.jivesoftware.smackx.vcard.VCardListener;
import org.jivesoftware.smackx.vcard.VCardManager;
import org.jivesoftware.smackx.vcard.VCardObject;
import org.jivesoftware.smackx.vcardextend.VCardExtendListener;
import org.jivesoftware.smackx.vcardextend.VCardExtendManager;
import org.jivesoftware.smackx.vcardextend.VCardExtendObject;

/**
 * 从服务端拉取用户信息
 */
public class FetchLoginUserRunnable implements Runnable {

    private final String mJid;
    private final OnFetchLoginUserListener mListener;

    public FetchLoginUserRunnable(String jid, OnFetchLoginUserListener l) {
        mJid = jid;
        mListener = l;
    }

    @Override
    public void run() {
        final LoginUser user = new LoginUser();
        fetchVCardObject(user);
        fetchVCardExtendObject(user);
        if (mListener != null) {
            mListener.onFetchLoginUser(user);
        }
    }

    private void fetchVCardObject(final LoginUser user) {
        final VCardManager vCardManager = XMPPManager.getInstance().getvCardManager();
        VCardListener vCardListener = new VCardListener() {
            @Override
            public void notifyFetchVCardResult(String jid, VCardObject vcard) {
                if (mJid.equals(jid)) {
                    if (vcard == null) {
                        Debug.print("SMACK Error, vcard is null:" + jid);
                        Debug.printStackTrace();
                    }
                    user.setVCardObject(vcard);
                    vCardManager.removeListener(this);
                    synchronized (user) {
                        try {
                            user.notifyAll();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void notifySetMyVCardResult(boolean success) {
            }
        };
        vCardManager.addListener(vCardListener);
        VCardObject vCardObject = vCardManager.fetchVCard(mJid);
        if (vCardObject == null) {
            synchronized (user) {
                try {
                    user.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            user.setVCardObject(vCardObject);
        }
    }

    private void fetchVCardExtendObject(final LoginUser user) {
        final VCardExtendManager vCardManager = XMPPManager.getInstance().getvCardExtendManager();
        VCardExtendListener vCardListener = new VCardExtendListener() {
            @Override
            public void notifyFetchVCardExtendResult(String jid, VCardExtendObject vcardExtend) {
                if (mJid.equals(jid)) {
                    user.setVCardExtendObject(vcardExtend);
                    vCardManager.removeListener(this);
                    synchronized (user) {
                        try {
                            user.notifyAll();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void notifySetMyVCardExtendResult(boolean success) {

            }

        };
        vCardManager.addListener(vCardListener);
        VCardExtendObject vCardExtendObject = vCardManager.fetchVCardExtend(mJid);
        if (vCardExtendObject == null) {
            synchronized (user) {
                try {
                    user.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            user.setVCardExtendObject(vCardExtendObject);
        }
    }


}