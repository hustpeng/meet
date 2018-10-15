package com.agmbat.meetyou.discovery.meeting;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public class MeetingListAdapter extends ArrayAdapter<MeetingItem> {

    public MeetingListAdapter(Context context, List<MeetingItem> list) {
        super(context, 0, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new MeetingItemView(getContext());
        }
        MeetingItemView view = (MeetingItemView) convertView;
        view.update(getItem(position));
        return convertView;
    }

}
