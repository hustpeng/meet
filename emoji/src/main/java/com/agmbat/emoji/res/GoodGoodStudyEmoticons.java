package com.agmbat.emoji.res;

import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.EditText;

import com.agmbat.android.AppResources;
import com.agmbat.emoji.display.EmojiDisplayProcessor;
import com.agmbat.emoji.display.EmoticonFilter;
import com.agmbat.emoji.panel.EmoticonClickListener;
import com.agmbat.emoji.panel.p2.EmoticonPageEntity;
import com.agmbat.emoji.panel.p2.EmoticonPageSetEntity;
import com.agmbat.emoji.panel.p2.page.BigEmoticonsAndTitleAdapter;
import com.agmbat.emoji.panel.p2.page.GridAdapterFactory;

import java.io.InputStream;

/**
 * 我们爱学习表情集
 */
public class GoodGoodStudyEmoticons implements EmojiResProvider {

    /**
     * 获取好好学习的表情集
     *
     * @return
     */
    @Override
    public EmoticonPageSetEntity getEmoticonPageSetEntity() {
        String xmlFilePath = "emoji/goodgoodstudy/goodgoodstudy.xml";
        XmlUtil xmlUtil = new XmlUtil();
        InputStream inputStream = AppResources.openAssetFile(xmlFilePath);
        EmoticonPageSetEntity emoticonPageSetEntity = xmlUtil.ParserXml("emoji/goodgoodstudy", inputStream);
        emoticonPageSetEntity.setGridAdapterFactory(new GridAdapterFactory() {
            @Override
            public BaseAdapter create(Context context, EmoticonPageEntity emoticonPageEntity, EmoticonClickListener onEmoticonClickListener) {
                return new BigEmoticonsAndTitleAdapter(context, emoticonPageEntity, onEmoticonClickListener);
            }
        });
        emoticonPageSetEntity.preparePageEntity();
        return emoticonPageSetEntity;
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
