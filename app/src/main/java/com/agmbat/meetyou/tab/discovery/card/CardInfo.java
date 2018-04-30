package com.agmbat.meetyou.tab.discovery.card;

import android.graphics.drawable.Drawable;
import android.view.View;

public class CardInfo {

    public String mTitle;
    public Drawable mIcon;
    public View.OnClickListener mOnClickListener;

    public CardInfo(String title) {
        mTitle = title;
    }
}
