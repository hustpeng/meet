package com.agmbat.meetyou.group;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.agmbat.imsdk.asmack.roster.ContactGroup;
import com.agmbat.imsdk.asmack.roster.ContactInfo;
import com.agmbat.meetyou.tab.contacts.ContactsGroupView;
import com.agmbat.meetyou.tab.contacts.ContactsView;

import java.util.List;

public class GroupAdapter extends BaseExpandableListAdapter {

    private static final String TAG = GroupAdapter.class.getSimpleName();

    private final Context mContext;
    private final List<CircleGroup> mGroupList;

    public GroupAdapter(Context context, List<CircleGroup> groups) {
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
    public CircleGroup getGroup(int groupPosition) {
        return mGroupList.get(groupPosition);
    }

    @Override
    public CircleInfo getChild(int groupPosition, int childPosition) {
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
        CircleGroup groupHolder = getGroup(groupPosition);
        groupHolder.setExpanded(true);
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        CircleGroup groupHolder = getGroup(groupPosition);
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
            convertView = new CircleGroupView(mContext);
        }
        CircleGroupView view = (CircleGroupView) convertView;
        view.update(getGroup(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new CircleView(mContext);
        }
        CircleView view = (CircleView) convertView;
        CircleInfo contactInfo = getChild(groupPosition, childPosition);
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
    public void addAll(List<CircleGroup> list) {
        mGroupList.addAll(list);
    }

    public void updateGroup(CircleGroup group) {
        if (null != group) {
            int size = getGroupCount();
            for (int i = 0; i < size; i++) {
                CircleGroup currentGroup = mGroupList.get(i);
                if (currentGroup.getGroupName().equalsIgnoreCase(group.getGroupName())) {
                    mGroupList.set(i, group);
                    break;
                }
            }
        }
        notifyDataSetChanged();
    }

}
