package com.agmbat.meetyou.tab.found.card;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.agmbat.meetyou.R;
import com.agmbat.meetyou.tab.found.card.CardInfo;

import java.util.List;


/**
 * Header 卡片
 */
public class HeaderCard extends FrameLayout {



    /**
     * 卡片item列表
     */
    private List<CardInfo> mList;

    public HeaderCard(@NonNull Context context) {
        super(context);
        View.inflate(context, R.layout.found_list_header, this);
    }

    public void setHeaderItemList(List<CardInfo> list) {
        mList = list;
        if (mList != null) {
            View header1 = findViewById(R.id.header1);
            TextView textView = header1.findViewById(R.id.name);
            updateHeaderItem(textView, mList.get(0));
            textView = header1.findViewById(R.id.name2);
            updateHeaderItem(textView, mList.get(1));
            textView = header1.findViewById(R.id.name3);
            updateHeaderItem(textView, mList.get(2));
            textView = header1.findViewById(R.id.name4);
            updateHeaderItem(textView, mList.get(3));


            View header2 = findViewById(R.id.header2);
            textView = header2.findViewById(R.id.name);
            updateHeaderItem(textView, mList.get(4));
            textView = header2.findViewById(R.id.name2);
            updateHeaderItem(textView, mList.get(5));
            textView = header2.findViewById(R.id.name3);
            updateHeaderItem(textView, mList.get(6));
            textView = header2.findViewById(R.id.name4);
            updateHeaderItem(textView, mList.get(7));
        }
    }

    private void updateHeaderItem(TextView tv, CardInfo item) {
        tv.setText(item.mTitle);
        tv.setOnClickListener(item.mOnClickListener);
    }

}
