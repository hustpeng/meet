package com.agmbat.meetyou.discovery.search;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.agmbat.android.SysResources;
import com.agmbat.android.utils.DeviceUtils;
import com.agmbat.meetyou.R;
import com.agmbat.tagpicker.TagView;

import java.util.ArrayList;
import java.util.List;

public class TagSelectedView extends LinearLayout {

    public interface OnSelectedListener {
        public void onSelected(int index, String tag);
    }

    private String mSelectedTag;
    private List<String> mTagList = new ArrayList<>();

    private OnSelectedListener mOnSelectedListener;

    public TagSelectedView(Context context) {
        super(context);
        init();
    }

    public TagSelectedView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(LinearLayout.VERTICAL);
    }

    /**
     * 设置Tag list
     *
     * @param tagList
     */
    public void setTagList(List<String> tagList) {
        removeAllViews();
        int screenWidth = DeviceUtils.getScreenSize().x;

        int divWidth = (int) (5 * SysResources.dipToPixel(8));
        int itemWidth = (screenWidth - divWidth) / 4;
        int itemHeight = (int) SysResources.dipToPixel(28);
        LayoutParams itemParams = new LayoutParams(itemWidth, itemHeight);
        int margin = (int) SysResources.dipToPixel(4);
        itemParams.leftMargin = margin;
        itemParams.rightMargin = margin;

        LinearLayout lineContent = new LinearLayout(getContext());
        LayoutParams lineParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int padding = (int) SysResources.dipToPixel(4);
        lineContent.setPadding(padding, padding, padding, padding);
        addView(lineContent, lineParams);

        for (int i = 0; i < tagList.size(); i++) {
            final TagView tagView = (TagView) View.inflate(getContext(), R.layout.picker_tag_item, null);
            String item = tagList.get(i);
            tagView.setText(item);
            tagView.setTag(item);
            tagView.setGravity(Gravity.CENTER);
            tagView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    setSelectedTag((String) view.getTag());
                }
            });
            if (lineContent.getChildCount() < 4) {
                lineContent.addView(tagView, itemParams);
            } else {
                lineContent = new LinearLayout(getContext());
                lineContent.setPadding(padding, padding, padding, padding);
                addView(lineContent, lineParams);
                lineContent.addView(tagView, itemParams);
            }
        }
        mTagList = tagList;
    }


    @Override
    public void setEnabled(boolean enabled) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            ViewGroup line =(ViewGroup) getChildAt(i);
            int childCount = line.getChildCount();
            for (int j = 0; j < childCount; j++) {
                View view = line.getChildAt(j);
                view.setEnabled(enabled);
            }
        }
    }

    /**
     * 设置选中的tag
     *
     * @param tag
     */
    public void setSelectedTag(String tag) {
        if (!TextUtils.isEmpty(tag) && tag.equals(mSelectedTag)) {
            // 如果选中同一个, 则直接返回
            return;
        }
        if (!TextUtils.isEmpty(mSelectedTag)) {
            TagView tagView = (TagView) findViewWithTag(mSelectedTag);
            if (tagView != null) {
                tagView.setSelected(false);
            }
        }
        mSelectedTag = tag;
        TagView tagView = (TagView) findViewWithTag(mSelectedTag);
        if (tagView != null) {
            tagView.setSelected(true);
        }
        if (mOnSelectedListener != null) {
            int index = mTagList.indexOf(tag);
                mOnSelectedListener.onSelected(index, mSelectedTag);
        }
    }

    public void setSelectedTag(int index){
        if(index < 0 || index >= mTagList.size()){
            return;
        }
        setSelectedTag(mTagList.get(index));
    }

    public void setOnSelectedListener(OnSelectedListener l) {
        mOnSelectedListener = l;
    }

    public String getSelectedTag() {
        return mSelectedTag;
    }
}
