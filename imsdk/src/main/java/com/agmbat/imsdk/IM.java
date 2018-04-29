package com.agmbat.imsdk;

import com.agmbat.android.utils.UiUtils;
import com.agmbat.imsdk.asmack.RosterManager;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.asmack.api.OnFetchContactListener;
import com.agmbat.imsdk.asmack.api.XMPPApi;
import com.agmbat.imsdk.data.ContactInfo;
import com.agmbat.imsdk.db.ContactTableManager;
import com.agmbat.imsdk.db.MeetDatabase;
import com.agmbat.imsdk.imevent.ContactUpdateEvent;
import com.agmbat.imsdk.imevent.LoginUserUpdateEvent;
import com.agmbat.imsdk.imevent.PresenceSubscribeEvent;
import com.agmbat.imsdk.user.UserManager;
import com.agmbat.log.Log;
import com.agmbat.utils.ArrayUtils;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.util.XmppStringUtils;
import org.jivesoftware.smackx.vcard.VCardListener;
import org.jivesoftware.smackx.vcard.VCardObject;
import org.jivesoftware.smackx.vcardextend.VCardExtendListener;
import org.jivesoftware.smackx.vcardextend.VCardExtendObject;

import java.util.List;

/**
 * 对外暴露统一的接口
 */
public class IM {

    public static final IM INSTANCE = new IM();

    public static IM get() {
        return INSTANCE;
    }



    private static final String TAG = "IM";

    private RosterManager.IRosterListener mIRosterListener = new RosterManager.IRosterListener() {
        @Override
        public void onEntriesAdded(List<String> addresses) {
            final String last = ArrayUtils.selectedLast(addresses);
            if (last == null) {
                return;
            }
            for (final String address : addresses) {
                Log.d(TAG, "==>onEntriesAdded(Thread): [address=" + address);
                String bareAddress = XmppStringUtils.parseBareAddress(address);
                String userName = XmppStringUtils.parseName(address);
                XMPPApi.fetchContactInfo(address, new OnFetchContactListener() {
                    @Override
                    public void onFetchContactInfo(ContactInfo contactInfo) {
                        if (contactInfo != null) {
                            Log.d("contact:" + contactInfo.toString());
                            UserManager.getInstance().saveOrUpdateContact(contactInfo);
                        }
                        if (last.equals(address)) {
                            // 最后一个需要发送消息到UI
                            UserManager.getInstance().updateGroupList();
                            EventBus.getDefault().post(new ContactUpdateEvent());
                        }
                    }
                });
            }

        }

        @Override
        public void onEntriesUpdated(List<String> addresses) {

        }

        @Override
        public void onEntriesDeleted(List<String> addresses) {

        }

        @Override
        public void presenceSubscribe(final ContactInfo contactInfo) {
            // 需要用本地数据库存为列表
            UiUtils.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    UserManager.getInstance().addFriendRequest(contactInfo);
                    EventBus.getDefault().post(new PresenceSubscribeEvent(contactInfo));
                }
            });
        }
    };

    public void init() {
        XMPPManager.getInstance().getRosterManager().addRosterListener(mIRosterListener);
        // 初始化
        UserManager.getInstance();
    }


}
