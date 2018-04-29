package com.agmbat.meetyou.coins;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.agmbat.meetyou.R;
import com.agmbat.time.TimeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 积分记录item
 */
public class CoinsView extends FrameLayout {

    @BindView(R.id.name)
    TextView mNameView;

    @BindView(R.id.date)
    TextView mDateView;

    @BindView(R.id.coins)
    TextView mCoinsView;

    public CoinsView(@NonNull Context context) {
        super(context);
        View.inflate(context, R.layout.coins_list_item, this);
        ButterKnife.bind(this, this);
    }

    public void update(CoinsRecords item) {
        mNameView.setText(item.summary);
        String time = TimeUtils.formatTime(item.getTimeInMillis());
        mDateView.setText(time);
        if (item.coins > 0) {
            String text = "+" + String.valueOf(item.coins);
            mCoinsView.setText(text);
        } else {
            mCoinsView.setText(String.valueOf(item.coins));
        }
    }
}
