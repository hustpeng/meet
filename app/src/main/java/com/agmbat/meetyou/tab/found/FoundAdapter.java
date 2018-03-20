package com.agmbat.meetyou.tab.found;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.agmbat.meetyou.data.RecentChat;
import com.agmbat.meetyou.tab.msg.RecentMsgView;

import java.util.List;

public class FoundAdapter extends ArrayAdapter<FoundGroup> {

    public FoundAdapter(Context context, List<FoundGroup> contactList) {
        super(context, 0, contactList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = new FoundView(getContext());
        }
        FoundView view = (FoundView) convertView;
        FoundGroup group = getItem(position);
        view.update(group);
        return convertView;
    }

}
