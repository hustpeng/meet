package com.agmbat.meetyou.group;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.agmbat.imsdk.search.group.GroupInfo;

import java.util.List;

public class GroupInfoAdapter extends ArrayAdapter<GroupInfo> {

    public GroupInfoAdapter(Context context, List<GroupInfo> list) {
        super(context, 0, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new GroupView(getContext());
        }
        GroupView view = (GroupView) convertView;
        view.update(getItem(position));
        return convertView;
    }

}
