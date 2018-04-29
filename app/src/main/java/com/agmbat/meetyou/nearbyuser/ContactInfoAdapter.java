package com.agmbat.meetyou.nearbyuser;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.agmbat.imsdk.data.ContactInfo;

import java.util.List;

public class ContactInfoAdapter extends ArrayAdapter<ContactInfo> {

    public ContactInfoAdapter(Context context, List<ContactInfo> list) {
        super(context, 0, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new ContactView(getContext());
        }
        ContactView view = (ContactView) convertView;
        view.update(getItem(position));
        return convertView;
    }

}
