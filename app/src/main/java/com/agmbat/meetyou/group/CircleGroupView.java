package com.agmbat.meetyou.group;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.agmbat.meetyou.R;

/**
 * 联系人组名View
 */
public class CircleGroupView extends LinearLayout {

    private TextView mGroupNameView;
    private ImageView mGroupIndicatorView;

    public CircleGroupView(Context context) {
        super(context);
        View.inflate(context, R.layout.contacts_group_item, this);
        mGroupNameView = (TextView) findViewById(R.id.group_name);
        mGroupIndicatorView = (ImageView) findViewById(R.id.group_indicator);
    }

    public void update(CircleGroup groupHolder) {
        if (groupHolder.isExpanded()) {
            mGroupIndicatorView.setImageResource(R.drawable.group_expanded);
        } else {
            mGroupIndicatorView.setImageResource(R.drawable.group_collapsed);
        }
        mGroupNameView.setText(groupHolder.getDisplayGroupName());
    }

}
