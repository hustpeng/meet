package com.agmbat.imsdk.asmack;

import com.agmbat.imsdk.data.ContactInfo;

import java.util.List;

public interface IRosterListener {

    void onEntriesAdded(List<String> addresses);

    void onEntriesUpdated(List<String> addresses);

    void onEntriesDeleted(List<String> addresses);

    /**
     * 收到添加自己为好友的申请
     *
     * @param contactInfo
     */
    void presenceSubscribe(ContactInfo contactInfo);

//        void onPresenceChanged(PresenceAdapter presence);

}
