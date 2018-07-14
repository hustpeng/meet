package com.agmbat.emoji.res;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.widget.BaseAdapter;
import android.widget.EditText;

import com.agmbat.android.AppResources;
import com.agmbat.android.utils.ViewUtils;
import com.agmbat.emoji.DelBtnStatus;
import com.agmbat.emoji.EmojiBean;
import com.agmbat.emoji.display.EmojiDisplay;
import com.agmbat.emoji.display.EmojiDisplayProcessor;
import com.agmbat.emoji.display.EmoticonFilter;
import com.agmbat.emoji.panel.EmoticonClickListener;
import com.agmbat.emoji.panel.p2.EmoticonPageEntity;
import com.agmbat.emoji.panel.p2.EmoticonPageSetEntity;
import com.agmbat.emoji.panel.p2.page.EmoticonsAdapter;
import com.agmbat.emoji.panel.p2.page.GridAdapterFactory;
import com.nostra13.universalimageloader.core.download.Scheme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefEmoticons implements EmojiResProvider, EmojiDisplayProcessor {

    /**
     * 使用linkedHashMap,保证顺序
     */
    static final HashMap<String, String> sEmoticonHashMap = new LinkedHashMap<>();

    static {
        sEmoticonHashMap.put(fromCodePoint(0x1f604), "emoji/defemoji/emoji_0x1f604.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f603), "emoji/defemoji/emoji_0x1f603.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f60a), "emoji/defemoji/emoji_0x1f60a.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f609), "emoji/defemoji/emoji_0x1f609.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f60d), "emoji/defemoji/emoji_0x1f60d.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f618), "emoji/defemoji/emoji_0x1f618.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f61a), "emoji/defemoji/emoji_0x1f61a.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f61c), "emoji/defemoji/emoji_0x1f61c.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f61d), "emoji/defemoji/emoji_0x1f61d.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f633), "emoji/defemoji/emoji_0x1f633.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f614), "emoji/defemoji/emoji_0x1f614.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f60c), "emoji/defemoji/emoji_0x1f60c.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f612), "emoji/defemoji/emoji_0x1f612.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f61e), "emoji/defemoji/emoji_0x1f61e.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f623), "emoji/defemoji/emoji_0x1f623.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f622), "emoji/defemoji/emoji_0x1f622.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f602), "emoji/defemoji/emoji_0x1f602.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f62d), "emoji/defemoji/emoji_0x1f602.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f62a), "emoji/defemoji/emoji_0x1f62a.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f625), "emoji/defemoji/emoji_0x1f625.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f630), "emoji/defemoji/emoji_0x1f630.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f613), "emoji/defemoji/emoji_0x1f613.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f628), "emoji/defemoji/emoji_0x1f628.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f631), "emoji/defemoji/emoji_0x1f631.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f620), "emoji/defemoji/emoji_0x1f620.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f621), "emoji/defemoji/emoji_0x1f621.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f616), "emoji/defemoji/emoji_0x1f616.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f637), "emoji/defemoji/emoji_0x1f637.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f632), "emoji/defemoji/emoji_0x1f632.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f47f), "emoji/defemoji/emoji_0x1f47f.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f60f), "emoji/defemoji/emoji_0x1f60f.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f466), "emoji/defemoji/emoji_0x1f466.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f467), "emoji/defemoji/emoji_0x1f467.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f468), "emoji/defemoji/emoji_0x1f468.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f469), "emoji/defemoji/emoji_0x1f469.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f31f), "emoji/defemoji/emoji_0x1f31f.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f444), "emoji/defemoji/emoji_0x1f444.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f44d), "emoji/defemoji/emoji_0x1f44d.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f44e), "emoji/defemoji/emoji_0x1f44e.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f44c), "emoji/defemoji/emoji_0x1f44c.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f44a), "emoji/defemoji/emoji_0x1f44a.png");
        sEmoticonHashMap.put(fromChar((char) 0x270a), "emoji/defemoji/emoji_0x270a.png");
        sEmoticonHashMap.put(fromChar((char) 0x270c), "emoji/defemoji/emoji_0x270c.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f446), "emoji/defemoji/emoji_0x1f446.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f447), "emoji/defemoji/emoji_0x1f447.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f449), "emoji/defemoji/emoji_0x1f449.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f448), "emoji/defemoji/emoji_0x1f448.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f64f), "emoji/defemoji/emoji_0x1f64f.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f44f), "emoji/defemoji/emoji_0x1f44f.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f4aa), "emoji/defemoji/emoji_0x1f4aa.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f457), "emoji/defemoji/emoji_0x1f457.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f380), "emoji/defemoji/emoji_0x1f380.png");
        sEmoticonHashMap.put(fromChar((char) 0x2764), "emoji/defemoji/emoji_0x2764.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f494), "emoji/defemoji/emoji_0x1f494.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f48e), "emoji/defemoji/emoji_0x1f48e.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f436), "emoji/defemoji/emoji_0x1f436.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f431), "emoji/defemoji/emoji_0x1f431.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f339), "emoji/defemoji/emoji_0x1f339.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f33b), "emoji/defemoji/emoji_0x1f33b.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f341), "emoji/defemoji/emoji_0x1f341.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f343), "emoji/defemoji/emoji_0x1f343.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f319), "emoji/defemoji/emoji_0x1f319.png");
        sEmoticonHashMap.put(fromChar((char) 0x2600), "emoji/defemoji/emoji_0x2600.png");
        sEmoticonHashMap.put(fromChar((char) 0x2601), "emoji/defemoji/emoji_0x2601.png");
        sEmoticonHashMap.put(fromChar((char) 0x26a1), "emoji/defemoji/emoji_0x26a1.png");
        sEmoticonHashMap.put(fromChar((char) 0x2614), "emoji/defemoji/emoji_0x2614.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f47b), "emoji/defemoji/emoji_0x1f47b.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f385), "emoji/defemoji/emoji_0x1f385.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f381), "emoji/defemoji/emoji_0x1f381.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f4f1), "emoji/defemoji/emoji_0x1f4f1.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f50d), "emoji/defemoji/emoji_0x1f50d.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f4a3), "emoji/defemoji/emoji_0x1f4a3.png");
        sEmoticonHashMap.put(fromChar((char) 0x26bd), "emoji/defemoji/emoji_0x26bd.png");
        sEmoticonHashMap.put(fromChar((char) 0x2615), "emoji/defemoji/emoji_0x2615.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f37a), "emoji/defemoji/emoji_0x1f37a.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f382), "emoji/defemoji/emoji_0x1f382.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f3e0), "emoji/defemoji/emoji_0x1f3e0.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f697), "emoji/defemoji/emoji_0x1f697.png");
        sEmoticonHashMap.put(fromCodePoint(0x1f559), "emoji/defemoji/emoji_0x1f559.png");
    }

    private static final Pattern EMOJI_RANGE = Pattern.compile("[\\u20a0-\\u32ff\\ud83c\\udc00-\\ud83d\\udeff\\udbb9\\udce5-\\udbb9\\udcee]");

    private static Matcher getMatcher(CharSequence matchStr) {
        return EMOJI_RANGE.matcher(matchStr);
    }

    public static String fromChar(char ch) {
        return Character.toString(ch);
    }

    public static String fromCodePoint(int codePoint) {
        return newString(codePoint);
    }

    public static final String newString(int codePoint) {
        if (Character.charCount(codePoint) == 1) {
            return String.valueOf(codePoint);
        } else {
            return new String(Character.toChars(codePoint));
        }
    }

    /**
     * 获取emoji list
     *
     * @return
     */
    public static List<EmojiBean> getEmojiList() {
        List<EmojiBean> emojis = new ArrayList<>();
        Set<Map.Entry<String, String>> entries = sEmoticonHashMap.entrySet();
        for (Map.Entry<String, String> key : entries) {
            String path = key.getValue();
            EmojiBean bean = new EmojiBean(path, key.getKey());
            bean.setIconUri(Scheme.wrapUri("assets", path));
            emojis.add(bean);
        }
        return emojis;
    }

    @Override
    public EmoticonPageSetEntity getEmoticonPageSetEntity() {
        EmoticonPageSetEntity emojiPageSetEntity = new EmoticonPageSetEntity();
        emojiPageSetEntity.setLine(3);
        emojiPageSetEntity.setRow(7);
        emojiPageSetEntity.setEmoticonList(getEmojiList());
        emojiPageSetEntity.setShowDelBtn(DelBtnStatus.LAST);
        emojiPageSetEntity.setIconUri(Scheme.wrapUri("assets", "emoji/defemoji/icon_emoji.png"));
        emojiPageSetEntity.setGridAdapterFactory(new GridAdapterFactory() {
            @Override
            public BaseAdapter create(Context context, EmoticonPageEntity emoticonPageEntity, EmoticonClickListener onEmoticonClickListener) {
                return new EmoticonsAdapter(context, emoticonPageEntity, onEmoticonClickListener);
            }
        });
        emojiPageSetEntity.preparePageEntity();
        return emojiPageSetEntity;
    }


    @Override
    public EmoticonFilter getEmoticonFilter(EditText editText) {
        return new EmojiFilter(editText);
    }

    @Override
    public EmojiDisplayProcessor getEmojiDisplayProcessor() {
        return this;
    }

    @Override
    public void spannableFilter(Spannable spannable, CharSequence text, int fontSize) {
        Matcher m = getMatcher(text);
        while (m.find()) {
            String key = m.group();
            Drawable drawable = getDrawable(key);
            EmojiDisplay.emojiDisplay(spannable, drawable, fontSize, m.start(), m.end());
        }
    }

    public static class EmojiFilter extends EmoticonFilter {

        private int emojiSize = -1;

        public EmojiFilter(EditText text) {
            super(text);
        }

        @Override
        public void filter(EditText editText, CharSequence text, int start, int lengthBefore, int lengthAfter) {
            emojiSize = emojiSize == -1 ? ViewUtils.getFontHeight(editText) : emojiSize;
            EmojiDisplay.clearSpan(editText.getText(), start, text.toString().length());
            Matcher m = getMatcher(text.toString().substring(start, text.toString().length()));
            while (m.find()) {
                String key = m.group();
                Drawable drawable = getDrawable(key);
                EmojiDisplay.emojiDisplay(editText.getText(), drawable, emojiSize, start + m.start(), start + m.end());
            }
        }

    }

    private static Drawable getDrawable(String key) {
        String icon = sEmoticonHashMap.get(key);
        Drawable drawable = AppResources.getAssetDrawable(icon);
        return drawable;
    }


}
