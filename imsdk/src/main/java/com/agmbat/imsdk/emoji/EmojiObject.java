package com.agmbat.imsdk.emoji;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.agmbat.android.AppResources;

public class EmojiObject {

    public String mCodeName;
    public int mResId;

    public EmojiObject(String codeName, int resId) {
        mCodeName = codeName;
        mResId = resId;
    }

    public Drawable getDrawable() {
        Resources res = AppResources.getResources();
        Drawable drawable = res.getDrawable(mResId);
        return drawable;
    }

    public ImageSpan getImageSpan() {
        Drawable drawable = getDrawable();
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
        return span;
    }

    public SpannableString toSpannableString() {
        ImageSpan imageSpan = getImageSpan();
        SpannableString str = new SpannableString(mCodeName);
        str.setSpan(imageSpan, 0, mCodeName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return str;
    }
}
