package com.agmbat.imsdk.asmack.roster;

import com.agmbat.db.annotation.Column;
import com.agmbat.db.annotation.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * 当前用户的好友分组
 * A holder for a group name and is principal state. It is an helper class to manage the state of the tabs.
 */
@Table(name = "contact_group")
public class ContactGroup {

    @Column(name = "id", isId = true)
    private long mGroupId;

    /**
     * 组名
     */
    @Column(name = "name")
    private String mGroupName;

    /**
     * 用于UI显示,是否展开
     */
    private boolean mIsExpanded;

    /**
     * 分组列表
     */
    private List<ContactInfo> mContactList = new ArrayList<ContactInfo>();

    /**
     * 提供无参构造函数, 用于数据库访问
     */
    public ContactGroup() {
    }

    /**
     * Create a {@link ContactGroup}.
     *
     * @param group the group name
     */
    public ContactGroup(String group) {
        mGroupName = group;
    }

    public void setGroupName(String name) {
        mGroupName = name;
    }

    public String getGroupName() {
        return mGroupName;
    }

    public boolean isExpanded() {
        return mIsExpanded;
    }

    public void setExpanded(boolean isExpanded) {
        mIsExpanded = isExpanded;
    }

    public void setContactList(List<ContactInfo> contacts) {
        mContactList = contacts;
    }

    public List<ContactInfo> getContactList() {
        return mContactList;
    }

    public void addContact(ContactInfo contactInfo) {
        if (contactInfo != null && !mContactList.contains(contactInfo)) {
            mContactList.add(contactInfo);
        }
    }

    /**
     * 判断是否包含给定的用户
     *
     * @param jid
     * @return
     */
    public boolean containsContact(String jid) {
        return RosterHelper.findContactInfo(jid, mContactList) != null;
    }

    public boolean containsContact(ContactInfo contactInfo) {
        return mContactList.contains(contactInfo);
    }

    public int getContactCount() {
        return mContactList.size();
    }

    public ContactInfo getContactAt(int index) {
        return mContactList.get(index);
    }

    public void updateContact(ContactInfo contactInfo) {
        int index = mContactList.indexOf(contactInfo);
        mContactList.set(index, contactInfo);
    }

    public void removeContact(ContactInfo contactInfo) {
        mContactList.remove(contactInfo);
    }

    public void removeContactAt(int index) {
        mContactList.remove(index);
    }

    public void removeAllContact() {
        mContactList.clear();
    }

    /**
     * 获取显示名称
     *
     * @return
     */
    public String getDisplayGroupName() {
        String name = mGroupName;
        if (RosterManager.GROUP_FRIENDS.equals(name)) {
            name = "我的好友";
        } else if (RosterManager.GROUP_UNGROUPED.equals(name)) {
            name = "非好友(我同意加你好友了吗)";
        }
        return String.format("%s(%d)", name, getContactCount());
    }


    public long getGroupId() {
        return mGroupId;
    }

    public void setGroupId(long groupId) {
        mGroupId = groupId;
    }
}
