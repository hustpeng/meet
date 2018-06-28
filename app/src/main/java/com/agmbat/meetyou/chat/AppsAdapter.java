package com.agmbat.meetyou.chat;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.agmbat.meetyou.R;
import com.agmbat.menu.MenuInfo;

import java.util.List;

public class AppsAdapter extends ArrayAdapter<MenuInfo> {

    public AppsAdapter(Context context, List<MenuInfo> data) {
        super(context, 0, data);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(getContext(), R.layout.item_app, null);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final MenuInfo appBean = getItem(position);
        viewHolder.imageView.setBackgroundDrawable(appBean.getIcon());
        viewHolder.textView.setText(appBean.getTitle());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appBean.performClick(position);
            }
        });
        return convertView;
    }

    static class ViewHolder {
        public ImageView imageView;
        public TextView textView;
    }
}