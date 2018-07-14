package com.agmbat.tagpicker;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.agmbat.android.SysResources;
import com.agmbat.android.utils.DeviceUtils;

import java.util.List;

/**
 * 带标题的tag面板
 */
public class TagCategoryView extends LinearLayout {

    private OnCheckedChangeListener mOnCheckedChangeListener;

    public TagCategoryView(Context context) {
        super(context);
        setOrientation(LinearLayout.VERTICAL);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener l) {
        mOnCheckedChangeListener = l;
    }

    /**
     * 创建类型名称
     *
     * @param category
     * @return
     */
    private LinearLayout createCategoryView(String category) {
        int color = 0xff959595;
        LinearLayout contentView = new LinearLayout(getContext());
        contentView.setGravity(Gravity.CENTER_VERTICAL);
        View leftLine = new View(getContext());
        leftLine.setBackgroundColor(color);

        View rightLine = new View(getContext());
        rightLine.setBackgroundColor(color);

        LayoutParams params = new LayoutParams(0, 1);
        params.weight = 1;
        params.leftMargin = (int) SysResources.dipToPixel(8);
        params.rightMargin = (int) SysResources.dipToPixel(8);

        TextView categoryView = new TextView(getContext());
        categoryView.setText(category);
        categoryView.setGravity(Gravity.CENTER);
        categoryView.setTextColor(color);
        contentView.addView(leftLine, params);
        contentView.addView(categoryView);
        contentView.addView(rightLine, params);
        return contentView;
    }

    /**
     * 设置Tag
     *
     * @param categoryTag
     */
    public void setCategoryTag(CategoryTag categoryTag) {
        removeAllViews();
        List<String> tagList = categoryTag.mTagList;
        if (tagList.size() == 0) {
            return;
        }

        int screenWidth = DeviceUtils.getScreenSize().x;
        View createCategoryView = createCategoryView(categoryTag.mCategory);
        int titleHeight = (int) SysResources.dipToPixel(50);
        addView(createCategoryView, new LayoutParams(screenWidth, titleHeight));

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
                    tagView.toggle();
                    if (mOnCheckedChangeListener != null) {
                        mOnCheckedChangeListener.onCheckedChanged(tagView, tagView.isChecked());
                    }
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
    }

    public void setCheckedList(List<String> list) {
        for (String item : list) {
            TagView tagView = (TagView) findViewWithTag(item);
            if (tagView != null) {
                tagView.setChecked(true);
            }
        }
    }
}
