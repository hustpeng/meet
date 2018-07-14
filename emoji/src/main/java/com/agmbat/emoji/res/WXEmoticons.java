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
import com.agmbat.emoji.panel.p2.page.BigEmoticonsAdapter;
import com.agmbat.emoji.panel.p2.page.GridAdapterFactory;

import java.io.InputStream;

/**
 * 微信表情集
 */
public class WXEmoticons implements EmojiResProvider {

    /**
     * 获取微信
     *
     * @return
     */
    @Override
    public EmoticonPageSetEntity getEmoticonPageSetEntity() {
        String xmlFilePath = "emoji/wxemoticons/wxemoticons.xml";
        XmlUtil xmlUtil = new XmlUtil();
        InputStream inputStream = AppResources.openAssetFile(xmlFilePath);
        EmoticonPageSetEntity emoticonPageSetEntity = xmlUtil.ParserXml("emoji/wxemoticons", inputStream);
        emoticonPageSetEntity.setGridAdapterFactory(new GridAdapterFactory() {
            @Override
            public BaseAdapter create(Context context, EmoticonPageEntity emoticonPageEntity, EmoticonClickListener onEmoticonClickListener) {
                return new BigEmoticonsAdapter(context, emoticonPageEntity, onEmoticonClickListener);
            }
        });
        emoticonPageSetEntity.preparePageEntity();
        return emoticonPageSetEntity;
    }

    @Override
    public EmoticonFilter getEmoticonFilter(EditText editText) {
        return new DefXhsEmoticons.XhsFilter(editText);
    }

    @Override
    public EmojiDisplayProcessor getEmojiDisplayProcessor() {
        return null;
    }

}
