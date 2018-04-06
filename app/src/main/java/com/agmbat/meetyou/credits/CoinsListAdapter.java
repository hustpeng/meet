package com.agmbat.meetyou.credits;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CoinsListAdapter extends ArrayAdapter<CoinsRecords> {

    public CoinsListAdapter(Context context, List<CoinsRecords> list) {
        super(context, 0, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new TextView(getContext());
        }
        TextView textView = (TextView) convertView;
        CoinsRecords item = getItem(position);
        textView.setText(item.summary);
        return convertView;
    }

}
