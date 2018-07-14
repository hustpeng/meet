package com.agmbat.emoji.res;

import android.widget.EditText;

import com.agmbat.emoji.display.EmojiDisplayProcessor;
import com.agmbat.emoji.display.EmoticonFilter;
import com.agmbat.emoji.panel.p2.EmoticonPageSetEntity;

/**
 * 表情资源提供
 */
public interface EmojiResProvider {

    /**
     * 提供表情面板资源配置
     *
     * @return
     */
    public EmoticonPageSetEntity getEmoticonPageSetEntity();

    /**
     * 提供显示时的过虑器
     *
     * @return
     */
    public EmoticonFilter getEmoticonFilter(EditText editText);

    /**
     * 获取文本显示时处理
     *
     * @return
     */
    public EmojiDisplayProcessor getEmojiDisplayProcessor();
}
