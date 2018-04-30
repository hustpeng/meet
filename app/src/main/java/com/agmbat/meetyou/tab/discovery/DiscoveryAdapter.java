package com.agmbat.meetyou.tab.discovery;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;


import java.util.List;

public class DiscoveryAdapter extends ArrayAdapter<DiscoveryGroup> {

    public DiscoveryAdapter(Context context, List<DiscoveryGroup> contactList) {
        super(context, 0, contactList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = new DiscoveryView(getContext());
        }
        DiscoveryView view = (DiscoveryView) convertView;
        DiscoveryGroup group = getItem(position);
        view.update(group);
        return convertView;
    }

}
