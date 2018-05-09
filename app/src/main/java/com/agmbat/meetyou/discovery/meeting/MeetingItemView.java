package com.agmbat.meetyou.discovery.meeting;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.agmbat.meetyou.R;
import com.agmbat.meetyou.coins.CoinsRecords;
import com.agmbat.time.TimeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 聚会活动item
 */
public class MeetingItemView extends FrameLayout {

    @BindView(R.id.name)
    TextView mNameView;

    @BindView(R.id.city)
    TextView mCityView;

    @BindView(R.id.enable_signup)
    TextView mEnableSignupView;

    public MeetingItemView(@NonNull Context context) {
        super(context);
        View.inflate(context, R.layout.meeting_list_item, this);
        ButterKnife.bind(this, this);
    }

    public void update(MeetingItem item) {
        mNameView.setText(item.title);
        mCityView.setText(item.city);
        String text = null;
        if (item.enableSignup) {
            text = "正在报名中";
        } else {
            text = "未开始报名";
        }
        mEnableSignupView.setText(text);
    }
}
