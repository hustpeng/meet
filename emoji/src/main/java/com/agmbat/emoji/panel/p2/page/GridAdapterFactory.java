package com.agmbat.emoji.panel.p2.page;

import android.content.Context;
import android.widget.BaseAdapter;

import com.agmbat.emoji.panel.EmoticonClickListener;
import com.agmbat.emoji.panel.p2.EmoticonPageEntity;

/**
 * 由于各page显示样式不一致, adapter创建由PageSetEntity来配置
 */
public interface GridAdapterFactory {

    public BaseAdapter create(Context context, EmoticonPageEntity emoticonPageEntity, EmoticonClickListener onEmoticonClickListener);
}
