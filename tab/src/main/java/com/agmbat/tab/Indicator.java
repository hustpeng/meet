/**
 * Copyright (C) 2015 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 * <p>
 * Tab Manager
 *
 * @author mayimchen
 * @since 2016-05-28
 */
package com.agmbat.tab;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.agmbat.android.SysResources;

/**
 * How to create a tab indicator
 */
public class Indicator {

    private CharSequence mLabel;
    private Drawable mIcon;
    private View mView;

    public Indicator(CharSequence label) {
        mLabel = label;
    }

    public Indicator(CharSequence label, Drawable icon) {
        mLabel = label;
        mIcon = icon;
    }

    public Indicator(View view) {
        mView = view;
    }

    public View createIndicatorView(Context context) {
        // 优先返回已设置的view
        if (mView != null) {
            return mView;
        }
        View tabIndicator = View.inflate(context, SysResources.getSysLayoutId("tab_indicator"), null);
        final TextView tv = (TextView) tabIndicator.findViewById(android.R.id.title);
        tv.setText(mLabel);
        final ImageView iconView = (ImageView) tabIndicator.findViewById(android.R.id.icon);
        iconView.setImageDrawable(mIcon);
        mView = tabIndicator;
        return mView;
    }
}