package com.agmbat.meetyou.discovery.filter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.agmbat.meetyou.R;

/**
 * 显示过虑器的view
 */
public class FilterView extends FrameLayout {

    /**
     * 过虑器内容变化监听器
     */
    private OnFilterInfoChangeListener mListener;

    private FilterInfo mFilterInfo;


    public FilterView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public FilterView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void setOnFilterInfoChangeListener(OnFilterInfoChangeListener l) {
        mListener = l;
    }

    private void init(Context context) {
        View.inflate(context, R.layout.view_filter, this);
        mFilterInfo = new FilterInfo();
    }

    public FilterInfo getFilterInfo() {
        return mFilterInfo;
    }
}
