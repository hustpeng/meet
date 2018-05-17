package com.agmbat.imsdk.asmack;

import com.agmbat.android.task.AsyncTask;
import com.agmbat.android.task.AsyncTaskUtils;
import com.agmbat.imsdk.data.ContactGroup;
import com.agmbat.imsdk.data.ContactInfo;
import com.agmbat.imsdk.db.ContactDBCache;
import com.agmbat.imsdk.imevent.ContactGroupLoadEvent;
import com.agmbat.imsdk.user.OnLoadContactGroupListener;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactManager {

    /**
     * 缓存
     */
    private static Map<String, ContactInfo> CONTACT_MAP = new HashMap<>();


    private static List<ContactGroup> CROUP_CACHE = null;

    /**
     * 获取联系人信息
     *
     * @param jid
     * @return
     */
    public static ContactInfo getContactInfo(String jid) {
        return CONTACT_MAP.get(jid);
    }

    public static void addContactInfo(ContactInfo contactInfo) {
        CONTACT_MAP.put(contactInfo.getBareJid(), contactInfo);
    }

    public static void addContactInfo(String jid, ContactInfo contactInfo) {
        CONTACT_MAP.put(jid, contactInfo);
    }

    public static void addGroupList(List<ContactGroup> result) {
        for (ContactGroup group : result) {
            for (ContactInfo info : group.getContactList()) {
                addContactInfo(info);
            }
        }
    }

    /**
     * 加载缓存中联系人列表
     *
     * @param l
     */
    public static void loadContactGroup() {
        if (CROUP_CACHE != null) {
            EventBus.getDefault().post(new ContactGroupLoadEvent(CROUP_CACHE));
            return;
        }
        AsyncTaskUtils.executeAsyncTask(new AsyncTask<Void, Void, List<ContactGroup>>() {
            @Override
            protected List<ContactGroup> doInBackground(Void... voids) {
                return ContactDBCache.getGroupList();
            }

            @Override
            protected void onPostExecute(List<ContactGroup> result) {
                super.onPostExecute(result);
                CROUP_CACHE = result;
                addGroupList(result);
                EventBus.getDefault().post(new ContactGroupLoadEvent(CROUP_CACHE));
            }
        });
    }
}
