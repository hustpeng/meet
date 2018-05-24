package com.agmbat.imsdk.asmack.api;

import com.agmbat.imsdk.asmack.roster.ContactInfo;

/**
 * 获取联系人回调
 */
public interface OnFetchContactListener {

    /**
     * 获取到联系人
     *
     * @param contactInfo
     */
    public void onFetchContactInfo(ContactInfo contactInfo);
}