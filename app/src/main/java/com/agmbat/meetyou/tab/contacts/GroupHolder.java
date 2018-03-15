package com.agmbat.meetyou.tab.contacts;

import android.content.Context;

import com.agmbat.android.AppResources;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.data.ContactInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A holder for a group name and is principal state. It is an helper class to manage the state of the tabs.
 */
public class GroupHolder {

    public static final String GROUP_RECENTLY = "Recently";
    public static final String GROUP_FRIENDS = "Hotlist";
    public static final String GROUP_BLOCK = "Block";

    private final String mGroupName;
    private boolean mIsExpanded;
    private List<ContactInfo> mContacts = new ArrayList<ContactInfo>();

    /**
     * Create a {@link GroupHolder}.
     *
     * @param group the group name
     */
    public GroupHolder(String group) {
        mGroupName = group;
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
        mContacts = contacts;
    }

    public List<ContactInfo> getContactList() {
        return mContacts;
    }

    public void addContact(ContactInfo contactInfo) {
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

    public ContactInfo getContactAt(int index) {
        return mContacts.get(index);
    }

    public void updateContact(ContactInfo contactInfo) {
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

    public String getDisplayGroup() {
        return String.format("%s(%d/%d)", getDisplayGroupName(), 1, getContactCount());
    }

    private String getDisplayGroupName() {
        Context context = AppResources.getAppContext();
        if (GROUP_RECENTLY.equals(mGroupName)) {
            return context.getString(R.string.contacts_recently);
        } else if (GROUP_FRIENDS.equals(mGroupName)) {
            return context.getString(R.string.contacts_hotlist);
        } else if (GROUP_BLOCK.equals(mGroupName)) {
            return context.getString(R.string.contacts_block);
        }
        return "";
    }


}
