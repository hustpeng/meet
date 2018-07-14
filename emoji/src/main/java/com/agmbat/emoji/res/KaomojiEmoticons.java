package com.agmbat.emoji.res;

import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.EditText;

import com.agmbat.android.AppResources;
import com.agmbat.emoji.EmojiBean;
import com.agmbat.emoji.display.EmojiDisplayProcessor;
import com.agmbat.emoji.display.EmoticonFilter;
import com.agmbat.emoji.panel.EmoticonClickListener;
import com.agmbat.emoji.panel.p2.EmoticonPageEntity;
import com.agmbat.emoji.panel.p2.EmoticonPageSetEntity;
import com.agmbat.emoji.panel.p2.page.GridAdapterFactory;
import com.agmbat.emoji.panel.p2.page.TextEmoticonsAdapter;
import com.nostra13.universalimageloader.core.download.Scheme;

import java.util.ArrayList;

/**
 * 颜文字表情集
 */
public class KaomojiEmoticons implements EmojiResProvider {

    /**
     * 插入颜文字表情集
     */
    @Override
    public EmoticonPageSetEntity getEmoticonPageSetEntity() {
        ArrayList<EmojiBean> textEmotionArray = new ArrayList<>();
        String text = AppResources.readAssetFile("emoji/txtemoji/kaomoji.txt");
        String[] array = text.split("\n");
        for (String line : array) {
            EmojiBean bean = new EmojiBean(line.trim());
            textEmotionArray.add(bean);
        }

        EmoticonPageSetEntity kaomojiPageSetEntity = new EmoticonPageSetEntity();
        kaomojiPageSetEntity.setLine(3);
        kaomojiPageSetEntity.setRow(3);
        kaomojiPageSetEntity.setEmoticonList(textEmotionArray);
        kaomojiPageSetEntity.setIconUri(Scheme.wrapUri("assets", "emoji/txtemoji/icon_kaomoji.png"));
        kaomojiPageSetEntity.setGridAdapterFactory(new GridAdapterFactory() {
            @Override
            public BaseAdapter create(Context context, EmoticonPageEntity emoticonPageEntity, EmoticonClickListener onEmoticonClickListener) {
                return new TextEmoticonsAdapter(context, emoticonPageEntity, onEmoticonClickListener);
            }
        });
        kaomojiPageSetEntity.preparePageEntity();
        return kaomojiPageSetEntity;
    }

    @Override
    public EmoticonFilter getEmoticonFilter(EditText editText) {
        return null;
    }

    @Override
    public EmojiDisplayProcessor getEmojiDisplayProcessor() {
        return null;
    }
}
