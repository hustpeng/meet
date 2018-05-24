package com.agmbat.imsdk.user;

import com.agmbat.imsdk.asmack.roster.ContactInfo;

import org.jivesoftware.smackx.vcard.VCardObject;

/**
 * 用户辅助工具类
 */
public class UserHelper {

    /**
     * 将vCardObject内容应用到ContactInfo中
     *
     * @param contactInfo
     * @param vCardObject
     */
    public static void applyVCardObject(ContactInfo contactInfo, VCardObject vCardObject) {
        if (vCardObject != null) {
            contactInfo.setBareJid(vCardObject.getJid());
            contactInfo.setNickname(vCardObject.getNickname());
            contactInfo.setAvatar(vCardObject.getAvatar());
            contactInfo.setGender(vCardObject.getGender());
        }
    }

    /**
     * 将user信息中提取出 vcard 信息
     *
     * @param user
     * @return
     */
    public static VCardObject getVCardObject(ContactInfo user) {
        return null;
    }
}
