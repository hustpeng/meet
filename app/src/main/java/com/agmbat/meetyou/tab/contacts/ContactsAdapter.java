
package com.agmbat.meetyou.tab.contacts;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.agmbat.imsdk.data.ContactInfo;
import com.agmbat.imsdk.data.ContactGroup;

import java.util.List;

public class ContactsAdapter extends BaseExpandableListAdapter {

    private static final String TAG = ContactsAdapter.class.getSimpleName();

    public static final int GROUP_INDEX_HOTLIST = 0;
    public static final int GROUP_INDEX_RECENTLY = 1;
    public static final int GROUP_INDEX_BLOCK = 2;

    private final Context mContext;
    private final List<ContactGroup> mGroupList;

    public ContactsAdapter(Context context, List<ContactGroup> groups) {
        mContext = context;
        mGroupList = groups;
    }

    @Override
    public int getGroupCount() {
        return mGroupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mGroupList.get(groupPosition).getContactCount();
    }

    @Override
    public ContactGroup getGroup(int groupPosition) {
        return mGroupList.get(groupPosition);
    }

    @Override
    public ContactInfo getChild(int groupPosition, int childPosition) {
        return mGroupList.get(groupPosition).getContactAt(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        ContactGroup groupHolder = getGroup(groupPosition);
        groupHolder.setExpanded(true);
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        ContactGroup groupHolder = getGroup(groupPosition);
        groupHolder.setExpanded(false);
    }

    @Override
    public long getCombinedChildId(long groupId, long childId) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long groupId) {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new ContactsGroupView(mContext);
        }
        ContactsGroupView view = (ContactsGroupView) convertView;
        view.update(getGroup(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new ContactsView(mContext);
        }
        ContactsView view = (ContactsView) convertView;
        ContactInfo contactInfo = getChild(groupPosition, childPosition);
        view.update(contactInfo);
        return convertView;
    }


    /**
     * 清空一次数据
     */
    public void clear() {
        mGroupList.clear();
    }

    /**
     * 更新数据
     *
     * @param list
     */
    public void addAll(List<ContactGroup> list) {
        mGroupList.addAll(list);
    }

    public void updateGroup(ContactGroup group) {
        if (null != group) {
            int size = getGroupCount();
            for (int i = 0; i < size; i++) {
                ContactGroup currentGroup = mGroupList.get(i);
                if (currentGroup.getGroupName().equalsIgnoreCase(group.getGroupName())) {
                    mGroupList.set(i, group);
                    break;
                }
            }
        }
        notifyDataSetChanged();
    }

}
