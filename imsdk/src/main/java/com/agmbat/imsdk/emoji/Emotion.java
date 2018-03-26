package com.agmbat.imsdk.emoji;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;

import com.agmbat.imsdk.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Emotion {

    public static final HashMap<String, EmojiObject> TEXT_TO_EMOTION = new HashMap<String, EmojiObject>();

    private static final String[] KEYS = new String[]{"/hi", "/wink", "/kiss", "/grin", "/gig", "/hug", "/shy",
            "/des", "/smile", "/cur", "/sur", "/bored", "/sed", "/drool", "/ll", "/flo", "/heart", "/hb", "/emb",
            "/tan", "/vomit", "/sad", "/cry", "/angry", "/shut", "/hs", "/party", "/eat", "/sle", "/bye"};

    private static final int[] VALUES = new int[]{R.drawable.emotion_1, R.drawable.emotion_2, R.drawable.emotion_3,
            R.drawable.emotion_4, R.drawable.emotion_5, R.drawable.emotion_6, R.drawable.emotion_7,
            R.drawable.emotion_8, R.drawable.emotion_9, R.drawable.emotion_10, R.drawable.emotion_11,
            R.drawable.emotion_12, R.drawable.emotion_13, R.drawable.emotion_14, R.drawable.emotion_15,
            R.drawable.emotion_16, R.drawable.emotion_17, R.drawable.emotion_18, R.drawable.emotion_19,
            R.drawable.emotion_20, R.drawable.emotion_21, R.drawable.emotion_22, R.drawable.emotion_23,
            R.drawable.emotion_24, R.drawable.emotion_25, R.drawable.emotion_26, R.drawable.emotion_27,
            R.drawable.emotion_28, R.drawable.emotion_29, R.drawable.emotion_30,};

    private static final List<EmojiObject> sEmojiList = new ArrayList<EmojiObject>();

    static {
        for (int i = 0; i < KEYS.length; i++) {
            EmojiObject object = new EmojiObject(KEYS[i], VALUES[i]);
            sEmojiList.add(object);
            TEXT_TO_EMOTION.put(KEYS[i], object);
        }
    }

    public static List<EmojiObject> getEmojiList() {
        return sEmojiList;
    }

    public static SpannableStringBuilder getEmojiText(String text) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        if (TextUtils.isEmpty(text)) {
            return builder;
        }

        builder.append(text);
        String subString = new String(text);
        int realIndex = 0;
        int subIndex = 0;
        do {
            subIndex = subString.indexOf("/");
            if (subIndex != -1) {
                subString = subString.substring(subIndex);
                realIndex += subIndex;
                boolean isEmotionText = false;
                String emotionText = "";

                for (int i = 0; i < KEYS.length; i++) {
                    if (subString.startsWith(KEYS[i])) {
                        EmojiObject object = TEXT_TO_EMOTION.get(KEYS[i]);
                        ImageSpan span = object.getImageSpan();
                        builder.setSpan(span, realIndex, realIndex + KEYS[i].length(),
                                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                        isEmotionText = true;
                        emotionText = KEYS[i];
                        break;
                    }
                }
                // 是表情，则让子字符串前进几位
                if (isEmotionText) {
                    subString = subString.substring(emotionText.length());
                    realIndex += emotionText.length();
                    // 不是表情，则让子字符串前进1位
                } else {
                    subString = subString.substring("/".length());
                    realIndex += 1;
                }
            }
        } while (subIndex != -1);
        return builder;
    }

}
