package com.agmbat.emoji.display;

import android.text.Spannable;

/**
 * emoji显示处理
 */
public interface EmojiDisplayProcessor {

    /**
     * 处理emoji 显示Spannale
     *
     * @param spannable
     * @param text
     * @param fontSize
     * @return
     */
    public void spannableFilter(Spannable spannable, CharSequence text, int fontSize);
}
