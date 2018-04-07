package com.agmbat.meetyou.coins;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public class CoinsListAdapter extends ArrayAdapter<CoinsRecords> {

    public CoinsListAdapter(Context context, List<CoinsRecords> list) {
        super(context, 0, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new CoinsView(getContext());
        }
        CoinsView view = (CoinsView) convertView;
        view.update(getItem(position));
        return convertView;
    }

}
