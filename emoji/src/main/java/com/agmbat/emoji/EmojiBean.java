package com.agmbat.emoji;

import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import java.util.Arrays;

/**
 * 表示emoji信息
 */
public class EmojiBean {

    /**
     * 表情图片,可插入到文本中
     */
    public static final int ACTION_TYPE_EMOJI_IMAGE = 1;

    /**
     * 表情文字
     */
    public static final int ACTION_TYPE_TEXT = 2;

    /**
     * 表情大图片, 不能插入到文字中, 发送处理方式与图片一样, 一次只能发送一张
     */
    public static final int ACTION_TYPE_BIG_IMAGE = 3;

    /**
     * 表情类型, 默认为小图片
     */
    private int mActionType = ACTION_TYPE_EMOJI_IMAGE;

    /**
     * emoji文本内容
     */
    private String mContent;

    /**
     * icon uri
     */
    private String mIconUri;

    public EmojiBean() {
    }

    public EmojiBean(String content) {
        mContent = content;
    }

    public EmojiBean(String iconUri, String content) {
        mIconUri = iconUri;
        mContent = content;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        this.mContent = content;
    }

    public String getIconUri() {
        return mIconUri;
    }

    public void setIconUri(String iconUri) {
        mIconUri = iconUri;
    }

    public int getActionType() {
        return mActionType;
    }

    public void setActionType(int actionType) {
        mActionType = actionType;
    }

    ///////emoji object
    public String mUnicode;
    public String mSBUnicode;
    public String mFileName;
    public int mCategory;

    private int[] mUnicodeArray;

    public EmojiBean(String code, String sbUnicode, String name, int category) {
        mUnicode = code;
        mFileName = name;
        mSBUnicode = sbUnicode;
        mCategory = category;

        String[] unicode = mUnicode.split("_");
        mUnicodeArray = new int[unicode.length];
        for (int i = 0; i < unicode.length; i++) {
            mUnicodeArray[i] = Integer.parseInt(unicode[i], 16);
        }
    }


    public String convertUnicode() {
        return newString(mUnicodeArray);
    }

    public boolean isUnicode(int... code) {
        return Arrays.equals(mUnicodeArray, code);
    }


    private static String newString(int[] unicodeArray) {
        int total = 0;
        char[][] charArray = new char[unicodeArray.length][];
        for (int i = 0; i < unicodeArray.length; i++) {
            char[] chs = Character.toChars(unicodeArray[i]);
            total += chs.length;
            charArray[i] = chs;
        }
        char[] r = new char[total];
        int k = 0;
        for (int i = 0; i < charArray.length; i++) {
            for (int j = 0; j < charArray[i].length; j++) {
                r[k] = charArray[i][j];
                k++;
            }
        }
        return new String(r);
    }

    private long mEventType;


    public long getEventType() {
        return mEventType;
    }

    public void setEventType(long eventType) {
        this.mEventType = eventType;
    }


    ///////
    public String mCodeName;

    public EmojiBean(String codeName, int resId) {
        mCodeName = codeName;
    }

    public ImageSpan getImageSpan() {
        Drawable drawable = null;
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
        return span;
    }

    public SpannableString toSpannableString2() {
        ImageSpan imageSpan = getImageSpan();
        SpannableString str = new SpannableString(mCodeName);
        str.setSpan(imageSpan, 0, mCodeName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return str;
    }
}
