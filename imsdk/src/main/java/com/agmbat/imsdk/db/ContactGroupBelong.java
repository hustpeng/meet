package com.agmbat.imsdk.db;

import com.agmbat.db.annotation.Column;
import com.agmbat.db.annotation.Table;

/**
 * 用户和分组的关联表
 */
@Table(name = "contact_group_belong")
public class ContactGroupBelong {

    @Column(name = "id", isId = true)
    private long mId;

    @Column(name = "group_id")
    public long mGroupId;

    @Column(name = "jid")
    public String mUserJid;

}
