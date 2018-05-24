package com.agmbat.imsdk.asmack.api;

import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.asmack.roster.ContactInfo;
import com.agmbat.imsdk.user.UserHelper;

import org.jivesoftware.smackx.vcard.VCardListener;
import org.jivesoftware.smackx.vcard.VCardManager;
import org.jivesoftware.smackx.vcard.VCardObject;

/**
 * 从服务端拉取用户信息
 */
public class FetchContactInfoRunnable implements Runnable {

    private final String contactJid;
    private final OnFetchContactListener listener;

    public FetchContactInfoRunnable(String contactJid, OnFetchContactListener listener) {
        this.contactJid = contactJid;
        this.listener = listener;
    }

    @Override
    public void run() {
        final VCardManager vCardManager = XMPPManager.getInstance().getvCardManager();
        final ContactInfo contactInfo = new ContactInfo();

        VCardListener vCardListener = new VCardListener() {
            @Override
            public void notifyFetchVCardResult(String jid, VCardObject vcard) {
                if (contactJid.equals(jid)) {
                    UserHelper.applyVCardObject(contactInfo, vcard);
                    vCardManager.removeListener(this);
                    synchronized (contactInfo) {
                        try {
                            contactInfo.notifyAll();
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
        VCardObject vCardObject = vCardManager.fetchVCard(contactJid);
        if (vCardObject == null) {
            synchronized (contactInfo) {
                try {
                    contactInfo.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            UserHelper.applyVCardObject(contactInfo, vCardObject);
        }
        if (listener != null) {
            listener.onFetchContactInfo(contactInfo);
        }
    }


}