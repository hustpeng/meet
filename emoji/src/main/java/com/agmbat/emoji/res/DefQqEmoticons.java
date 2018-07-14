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

public class DefQqEmoticons implements EmojiResProvider, EmojiDisplayProcessor {


    /**
     * 使用linkedHashMap,保证顺序
     */
    public static final HashMap<String, String> sQqEmoticonHashMap = new LinkedHashMap<>();

    static {
        sQqEmoticonHashMap.put("[ecf]", "ecf.png");
        sQqEmoticonHashMap.put("[ecv]", "ecv.png");
        sQqEmoticonHashMap.put("[ecb]", "ecb.png");
        sQqEmoticonHashMap.put("[ecy]", "ecy.png");
        sQqEmoticonHashMap.put("[ebu]", "ebu.png");
        sQqEmoticonHashMap.put("[ebr]", "ebr.png");
        sQqEmoticonHashMap.put("[ecc]", "ecc.png");
        sQqEmoticonHashMap.put("[eft]", "eft.png");
        sQqEmoticonHashMap.put("[ecr]", "ecr.png");
        sQqEmoticonHashMap.put("[ebs]", "ebs.png");
        sQqEmoticonHashMap.put("[ech]", "ech.png");
        sQqEmoticonHashMap.put("[ecg]", "ecg.png");
        sQqEmoticonHashMap.put("[ebh]", "ebh.png");
        sQqEmoticonHashMap.put("[ebg]", "ebg.png");
        sQqEmoticonHashMap.put("[ecp]", "ecp.png");
        sQqEmoticonHashMap.put("[deg]", "deg.png");
        sQqEmoticonHashMap.put("[ecd]", "ecd.png");
        sQqEmoticonHashMap.put("[ecj]", "ecj.png");
        sQqEmoticonHashMap.put("[ebv]", "ebv.png");
        sQqEmoticonHashMap.put("[ece]", "ece.png");
        sQqEmoticonHashMap.put("[ebl]", "ebl.png");
        sQqEmoticonHashMap.put("[eca]", "eca.png");
        sQqEmoticonHashMap.put("[ecn]", "ecn.png");
        sQqEmoticonHashMap.put("[eco]", "eco.png");
        sQqEmoticonHashMap.put("[eeo]", "eeo.png");
        sQqEmoticonHashMap.put("[eep]", "eep.png");
        sQqEmoticonHashMap.put("[eci]", "eci.png");
        sQqEmoticonHashMap.put("[ebj]", "ebj.png");
        sQqEmoticonHashMap.put("[eer]", "eer.png");
        sQqEmoticonHashMap.put("[edi]", "edi.png");
        sQqEmoticonHashMap.put("[ebq]", "ebq.png");
        sQqEmoticonHashMap.put("[eeq]", "eeq.png");
        sQqEmoticonHashMap.put("[ecq]", "ecq.png");
        sQqEmoticonHashMap.put("[ebt]", "ebt.png");
        sQqEmoticonHashMap.put("[ede]", "ede.png");
        sQqEmoticonHashMap.put("[eew]", "eew.png");
        sQqEmoticonHashMap.put("[eex]", "eex.png");
        sQqEmoticonHashMap.put("[dga]", "dga.png");
        sQqEmoticonHashMap.put("[ebp]", "ebp.png");
        sQqEmoticonHashMap.put("[ebo]", "ebo.png");
    }

    private static final Pattern QQ_RANGE = Pattern.compile("\\[[a-zA-Z0-9\\u4e00-\\u9fa5]+\\]");

    private static Drawable getDrawable(String key) {
        String icon = DefQqEmoticons.sQqEmoticonHashMap.get(key);
        Drawable drawable = AppResources.getAssetDrawable(icon);
        return drawable;
    }

    public static Matcher getMatcher(CharSequence matchStr) {
        return QQ_RANGE.matcher(matchStr);
    }

    /**
     * 获取emoji list
     *
     * @return
     */
    public static List<EmojiBean> getEmojiList() {
        List<EmojiBean> emojis = new ArrayList<>();
        Set<Map.Entry<String, String>> entries = sQqEmoticonHashMap.entrySet();
        for (Map.Entry<String, String> key : entries) {
            String path = key.getValue();
            EmojiBean bean = new EmojiBean(path, key.getKey());
            bean.setIconUri(Scheme.wrapUri("assets", "emoji/qqemoji/" + path));
            emojis.add(bean);
        }
        return emojis;
    }

    @Override
    public EmoticonPageSetEntity getEmoticonPageSetEntity() {
        EmoticonPageSetEntity kaomojiPageSetEntity = new EmoticonPageSetEntity();
        kaomojiPageSetEntity.setLine(3);
        kaomojiPageSetEntity.setRow(7);
        kaomojiPageSetEntity.setEmoticonList(getEmojiList());
        kaomojiPageSetEntity.setShowDelBtn(DelBtnStatus.LAST);
        kaomojiPageSetEntity.setIconUri(Scheme.wrapUri("assets", "kys"));
        kaomojiPageSetEntity.setGridAdapterFactory(new GridAdapterFactory() {
            @Override
            public BaseAdapter create(Context context, EmoticonPageEntity emoticonPageEntity, EmoticonClickListener onEmoticonClickListener) {
                EmoticonsAdapter adapter = new EmoticonsAdapter(context, emoticonPageEntity, onEmoticonClickListener);
//                adapter.setItemHeightMaxRatio(1.8);
                return adapter;
            }
        });
        kaomojiPageSetEntity.preparePageEntity();
        return kaomojiPageSetEntity;
    }

    @Override
    public EmoticonFilter getEmoticonFilter(EditText editText) {
        return new QqFilter(editText);
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

    public static class QqFilter extends EmoticonFilter {

        private int emoticonSize = -1;

        public QqFilter(EditText text) {
            super(text);
        }

        @Override
        public void filter(EditText editText, CharSequence text, int start, int lengthBefore, int lengthAfter) {
            emoticonSize = emoticonSize == -1 ? ViewUtils.getFontHeight(editText) : emoticonSize;
            EmojiDisplay.clearSpan(editText.getText(), start, text.toString().length());
            Matcher m = getMatcher(text.toString().substring(start, text.toString().length()));
            while (m.find()) {
                String key = m.group();
                Drawable drawable = getDrawable(key);
                EmojiDisplay.emojiDisplay(editText.getText(), drawable, emoticonSize, start + m.start(), start + m.end());
            }
        }

    }

}














