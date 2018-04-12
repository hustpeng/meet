package com.agmbat.imsdk;

import com.agmbat.imsdk.asmack.XMPPManager;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smackx.vcard.VCardListener;
import org.jivesoftware.smackx.vcard.VCardObject;

/**
 * 对外暴露统一的接口
 */
public class IM {

    public static final IM INSTANCE = new IM();

    public static IM get() {
        return INSTANCE;
    }

    private VCardListener mVCardListener = (new VCardListener() {

        @Override
        public void notifySetMyVCardResult(boolean success) {
            if (success) {
                VCardObject vcard = XMPPManager.getInstance().getvCardManager().fetchMyVCard();
                if (vcard != null) {
                    EventBus.getDefault().post(vcard);
                }
            }
        }

        @Override
        public void notifyFetchVCardResult(String jid, VCardObject vcard) {
            if (vcard != null) {
                EventBus.getDefault().post(vcard);
            }
        }
    });

    public void init() {
        XMPPManager.getInstance().getvCardManager().addListener(mVCardListener);
    }

    /**
     * 获取登陆后的用户信息
     */
    public void fetchMyVCard() {
        VCardObject vCardObject = XMPPManager.getInstance().getvCardManager().fetchMyVCard();
        if (vCardObject != null) {
            EventBus.getDefault().post(vCardObject);
        }
    }
}
