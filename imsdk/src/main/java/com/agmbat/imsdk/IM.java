package com.agmbat.imsdk;

import com.agmbat.android.utils.UiUtils;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.log.Log;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smackx.vcard.VCardListener;
import org.jivesoftware.smackx.vcard.VCardObject;
import org.jivesoftware.smackx.vcardextend.VCardExtendListener;
import org.jivesoftware.smackx.vcardextend.VCardExtendObject;

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

    private VCardExtendListener mVCardExtendListener = (new VCardExtendListener() {

        @Override
        public void notifySetMyVCardExtendResult(boolean success) {
            if (success) {
                VCardExtendObject vcard = XMPPManager.getInstance().getvCardExtendManager().fetchMyVCardExtend();
                if (vcard != null) {
                    EventBus.getDefault().post(vcard);
                }
            }
        }

        @Override
        public void notifyFetchVCardExtendResult(String jid, VCardExtendObject vcardExtend) {
            if (vcardExtend != null) {
                EventBus.getDefault().post(vcardExtend);
            }
        }
    });

    public void init() {
        XMPPManager.getInstance().getvCardManager().addListener(mVCardListener);
        XMPPManager.getInstance().getvCardExtendManager().addListener(mVCardExtendListener);
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

    /**
     * 获取登陆后的用户信息
     */
    public void fetchMyVCardExtend() {
        VCardExtendObject vCardObject = XMPPManager.getInstance().getvCardExtendManager().fetchMyVCardExtend();
        if (vCardObject != null) {
            EventBus.getDefault().post(vCardObject);
        }
    }
}
