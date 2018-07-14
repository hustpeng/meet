package com.agmbat.emoji.display;

import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;

import java.util.ArrayList;
import java.util.List;

public class EmojiDisplay {

    public static final int WRAP_DRAWABLE = -1;

    private static List<EmojiDisplayProcessor> sDisplayProcessorList = new ArrayList<>();

    public static void addEmojiDisplayProcessor(EmojiDisplayProcessor processor) {
        sDisplayProcessorList.add(processor);
    }

    /**
     * 更新文本, 用于显示
     *
     * @param text
     * @param fontSize
     * @return
     */
    public static Spannable update(String text, int fontSize) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
        for (EmojiDisplayProcessor processor : sDisplayProcessorList) {
            processor.spannableFilter(spannableStringBuilder, text, fontSize);
        }
        return spannableStringBuilder;
    }

    public static void emojiDisplay(Spannable spannable, Drawable drawable, int fontSize, int start, int end) {
        if (drawable != null) {
            int itemHeight;
            int itemWidth;
            if (fontSize == WRAP_DRAWABLE) {
                itemHeight = drawable.getIntrinsicHeight();
                itemWidth = drawable.getIntrinsicWidth();
            } else {
                itemHeight = fontSize;
                itemWidth = fontSize;
            }
            drawable.setBounds(0, 0, itemHeight, itemWidth);
            EmojiSpan imageSpan = new EmojiSpan(drawable);
            spannable.setSpan(imageSpan, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
    }

    public static void clearSpan(Spannable spannable, int start, int end) {
        if (start == end) {
            return;
        }
        EmojiSpan[] oldSpans = spannable.getSpans(start, end, EmojiSpan.class);
        for (int i = 0; i < oldSpans.length; i++) {
            spannable.removeSpan(oldSpans[i]);
        }
    }


}
