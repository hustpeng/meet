package com.agmbat.imsdk.asmack.roster;

import com.agmbat.db.annotation.Column;
import com.agmbat.db.annotation.Table;
import com.agmbat.imsdk.asmack.roster.ContactInfo;

/**
 * 好友申请信息
 * 由于ContactInfo属于UI使用的数据信息, 并且其他表也需要使用, 使用此数据类进行转换
 */
@Table(name = "friend_request")
public class FriendRequest {

    @Column(name = "id", isId = true)
    private int id;

    /**
     * 申请人jid
     */
    @Column(name = "jid")
    private String jid;

    /**
     * 申请人昵称
     */
    @Column(name = "nickname")
    private String nickname;

    /**
     * 好友备注
     */
    @Column(name = "remark")
    private String remark;


    /**
     * 用户头像url
     */
    @Column(name = "avatar")
    private String avatar;


    public FriendRequest() {
    }

    public FriendRequest(ContactInfo contactInfo) {
        jid = contactInfo.getBareJid();
        remark = contactInfo.getRemark();
        nickname = contactInfo.getNickName();
        avatar = contactInfo.getAvatar();
    }

    public ContactInfo toContactInfo() {
        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setBareJid(jid);
        contactInfo.setRemark(remark);
        contactInfo.setNickname(nickname);
        contactInfo.setAvatar(avatar);
        return contactInfo;
    }

    public String getJid() {
        return jid;
    }
}
