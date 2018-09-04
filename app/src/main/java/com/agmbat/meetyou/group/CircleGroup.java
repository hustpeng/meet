package com.agmbat.meetyou.group;

import com.agmbat.db.annotation.Column;
import com.agmbat.db.annotation.Table;
import com.agmbat.imsdk.asmack.roster.ContactInfo;
import com.agmbat.imsdk.group.CircleInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 当前用户的好友分组
 * A holder for a group name and is principal state. It is an helper class to manage the state of the tabs.
 */
@Table(name = "contact_group")
public class CircleGroup {

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
    private List<CircleInfo> mContacts = new ArrayList<CircleInfo>();

    /**
     * 提供无参构造函数, 用于数据库访问
     */
    public CircleGroup() {
    }

    /**
     * Create a {@link CircleGroup}.
     *
     * @param group the group name
     */
    public CircleGroup(String group) {
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

    public void setContactList(List<CircleInfo> contacts) {
        mContacts = contacts;
    }

    public List<CircleInfo> getContactList() {
        return mContacts;
    }

    public void addContact(CircleInfo contactInfo) {
        if (contactInfo != null && !mContacts.contains(contactInfo)) {
            mContacts.add(contactInfo);
        }
    }

    public boolean containsContact(ContactInfo contactInfo) {
        return mContacts.contains(contactInfo);
    }

    public int getContactCount() {
        return mContacts.size();
    }

    public CircleInfo getContactAt(int index) {
        return mContacts.get(index);
    }

    public void updateContact(CircleInfo contactInfo) {
        int index = mContacts.indexOf(contactInfo);
        mContacts.set(index, contactInfo);
    }

    public void removeContact(ContactInfo contactInfo) {
        mContacts.remove(contactInfo);
    }

    public void removeContactAt(int index) {
        mContacts.remove(index);
    }

    public void removeAllContact() {
        mContacts.clear();
    }

    /**
     * 获取显示名称
     *
     * @return
     */
    public String getDisplayGroupName() {
        return String.format("%s(%d)", mGroupName, getContactCount());
    }


    public long getGroupId() {
        return mGroupId;
    }

    public void setGroupId(long groupId) {
        mGroupId = groupId;
    }
}
