package com.agmbat.meetyou.tab.found;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.agmbat.meetyou.R;

import java.util.List;


/**
 * Header 卡片
 */
public class HeaderCard extends FrameLayout {

    public static class HeaderItemInfo {
        private String mTitle;
        private Drawable mIcon;
        private OnClickListener mOnClickListener;

        public HeaderItemInfo(String title) {
            mTitle = title;
        }
    }

    /**
     * 卡片item列表
     */
    private List<HeaderItemInfo> mList;

    public HeaderCard(@NonNull Context context) {
        super(context);
        View.inflate(context, R.layout.found_list_header, this);
    }

    public void setHeaderItemList(List<HeaderItemInfo> list) {
        mList = list;
        if (mList != null) {
            View header1 = findViewById(R.id.header1);
            TextView textView = header1.findViewById(R.id.name);
            textView.setText(mList.get(0).mTitle);
            textView = header1.findViewById(R.id.name2);
            textView.setText(mList.get(1).mTitle);
            textView = header1.findViewById(R.id.name3);
            textView.setText(mList.get(2).mTitle);
            textView = header1.findViewById(R.id.name4);
            textView.setText(mList.get(3).mTitle);

            View header2 = findViewById(R.id.header2);
            textView = header2.findViewById(R.id.name);
            textView.setText(mList.get(4).mTitle);
            textView = header2.findViewById(R.id.name2);
            textView.setText(mList.get(5).mTitle);
            textView = header2.findViewById(R.id.name3);
            textView.setText(mList.get(6).mTitle);
            textView = header2.findViewById(R.id.name4);
            textView.setText(mList.get(7).mTitle);
        }
    }

}
