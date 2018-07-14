package com.agmbat.emoji.res;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.TextUtils;
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

public class DefXhsEmoticons implements EmojiResProvider, EmojiDisplayProcessor {

    /**
     * 使用linkedHashMap,保证顺序
     */
    static final HashMap<String, String> sXhsEmoticonHashMap = new LinkedHashMap<>();

    static {
        sXhsEmoticonHashMap.put("[无语]", "emoji/xhsemoji/xhsemoji_1.png");
        sXhsEmoticonHashMap.put("[汗]", "emoji/xhsemoji/xhsemoji_2.png");
        sXhsEmoticonHashMap.put("[瞎]", "emoji/xhsemoji/xhsemoji_3.png");
        sXhsEmoticonHashMap.put("[口水]", "emoji/xhsemoji/xhsemoji_4.png");
        sXhsEmoticonHashMap.put("[酷]", "emoji/xhsemoji/xhsemoji_5.png");
        sXhsEmoticonHashMap.put("[哭]", "emoji/xhsemoji/xhsemoji_6.png");
        sXhsEmoticonHashMap.put("[萌]", "emoji/xhsemoji/xhsemoji_7.png");
        sXhsEmoticonHashMap.put("[挖鼻孔]", "emoji/xhsemoji/xhsemoji_8.png");
        sXhsEmoticonHashMap.put("[好冷]", "emoji/xhsemoji/xhsemoji_9.png");
        sXhsEmoticonHashMap.put("[白眼]", "emoji/xhsemoji/xhsemoji_10.png");
        sXhsEmoticonHashMap.put("[晕]", "emoji/xhsemoji/xhsemoji_11.png");
        sXhsEmoticonHashMap.put("[么么哒]", "emoji/xhsemoji/xhsemoji_12.png");
        sXhsEmoticonHashMap.put("[哈哈]", "emoji/xhsemoji/xhsemoji_13.png");
        sXhsEmoticonHashMap.put("[好雷]", "emoji/xhsemoji/xhsemoji_14.png");
        sXhsEmoticonHashMap.put("[啊]", "emoji/xhsemoji/xhsemoji_15.png");
        sXhsEmoticonHashMap.put("[嘘]", "emoji/xhsemoji/xhsemoji_16.png");
        sXhsEmoticonHashMap.put("[震惊]", "emoji/xhsemoji/xhsemoji_17.png");
        sXhsEmoticonHashMap.put("[刺瞎]", "emoji/xhsemoji/xhsemoji_18.png");
        sXhsEmoticonHashMap.put("[害羞]", "emoji/xhsemoji/xhsemoji_19.png");
        sXhsEmoticonHashMap.put("[嘿嘿]", "emoji/xhsemoji/xhsemoji_10.png");
        sXhsEmoticonHashMap.put("[嘻嘻]", "emoji/xhsemoji/xhsemoji_21.png");
    }

    private static final Pattern XHS_RANGE = Pattern.compile("\\[[a-zA-Z0-9\\u4e00-\\u9fa5]+\\]");

    private static Matcher getMatcher(CharSequence matchStr) {
        return XHS_RANGE.matcher(matchStr);
    }


    /**
     * 获取表情
     *
     * @return
     */
    @Override
    public EmoticonPageSetEntity getEmoticonPageSetEntity() {
        List<EmojiBean> emoticonList = getEmojiList();
        EmoticonPageSetEntity xhsPageSetEntity = new EmoticonPageSetEntity();
        xhsPageSetEntity.setLine(3);
        xhsPageSetEntity.setRow(7);
        xhsPageSetEntity.setEmoticonList(emoticonList);
        xhsPageSetEntity.setShowDelBtn(DelBtnStatus.LAST);
        xhsPageSetEntity.setIconUri(Scheme.wrapUri("assets", "emoji/xhsemoji/" + "xhsemoji_19.png"));
        xhsPageSetEntity.setGridAdapterFactory(new GridAdapterFactory() {
            @Override
            public BaseAdapter create(Context context, EmoticonPageEntity emoticonPageEntity, EmoticonClickListener onEmoticonClickListener) {
                return new EmoticonsAdapter(context, emoticonPageEntity, onEmoticonClickListener);
            }
        });
        xhsPageSetEntity.preparePageEntity();
        return xhsPageSetEntity;
    }


    @Override
    public EmoticonFilter getEmoticonFilter(EditText editText) {
        return new XhsFilter(editText);
    }

    @Override
    public EmojiDisplayProcessor getEmojiDisplayProcessor() {
        return this;
    }

    /**
     * 获取emoji list
     *
     * @return
     */
    public static List<EmojiBean> getEmojiList() {
        List<EmojiBean> emojis = new ArrayList<>();
        Set<Map.Entry<String, String>> entries = sXhsEmoticonHashMap.entrySet();
        for (Map.Entry<String, String> key : entries) {
            String path = key.getValue();
            EmojiBean bean = new EmojiBean(path, key.getKey());
            bean.setIconUri(Scheme.wrapUri("assets", path));
            emojis.add(bean);
        }
        return emojis;
    }


    @Override
    public void spannableFilter(Spannable spannable, CharSequence text, int fontSize) {
        Matcher m = getMatcher(text);
        while (m.find()) {
            String key = m.group();
            String icon = DefXhsEmoticons.sXhsEmoticonHashMap.get(key);
            if (!TextUtils.isEmpty(icon)) {
                Drawable drawable = AppResources.getAssetDrawable(icon);
                EmojiDisplay.emojiDisplay(spannable, drawable, fontSize, m.start(), m.end());
            }
        }
    }

    public static class XhsFilter extends EmoticonFilter {


        private int emoticonSize = -1;

        public XhsFilter(EditText text) {
            super(text);
        }

        @Override
        public void filter(EditText editText, CharSequence text, int start, int lengthBefore, int lengthAfter) {
            emoticonSize = emoticonSize == -1 ? ViewUtils.getFontHeight(editText) : emoticonSize;
            EmojiDisplay.clearSpan(editText.getText(), start, text.toString().length());
            Matcher m = getMatcher(text.toString().substring(start, text.toString().length()));
            while (m.find()) {
                String key = m.group();
                String icon = DefXhsEmoticons.sXhsEmoticonHashMap.get(key);
                if (!TextUtils.isEmpty(icon)) {
                    Drawable drawable = AppResources.getAssetDrawable(icon);
                    EmojiDisplay.emojiDisplay(editText.getText(), drawable, emoticonSize, start + m.start(), start + m.end());
                }
            }
        }

    }


}
