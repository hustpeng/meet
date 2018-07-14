package com.agmbat.emoji.panel.p2.page;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.agmbat.emoji.R;

/**
 * 表示一页的表情
 **/
public class EmoticonPageView extends RelativeLayout {

    private GridView mGridView;

    public EmoticonPageView(Context context) {
        this(context, null);
    }

    public EmoticonPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(getContext(), R.layout.item_emoticonpage, this);
        mGridView = (GridView) findViewById(R.id.gv_emotion);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            mGridView.setMotionEventSplittingEnabled(false);
        }
        mGridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        mGridView.setCacheColorHint(0);
        mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mGridView.setVerticalScrollBarEnabled(false);
    }

    public void setNumColumns(int row) {
        mGridView.setNumColumns(row);
    }

    public void setAdapter(BaseAdapter adapter) {
        mGridView.setAdapter(adapter);
    }

    /**
     * 配置adapter
     *
     * @param pageEntity
     * @param onEmoticonClickListener
     */
}
