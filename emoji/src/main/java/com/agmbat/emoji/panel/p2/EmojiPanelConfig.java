package com.agmbat.emoji.panel.p2;

import android.widget.EditText;

import com.agmbat.emoji.display.EmojiClickHandler;
import com.agmbat.emoji.display.EmojiDisplay;
import com.agmbat.emoji.display.EmojiDisplayProcessor;
import com.agmbat.emoji.display.EmoticonFilter;
import com.agmbat.emoji.panel.EmoticonClickListener;
import com.agmbat.emoji.res.EmojiResProvider;

import java.util.ArrayList;
import java.util.List;

public class EmojiPanelConfig {

    private EditText mEditText;
    private EmoticonClickListener mEmoticonClickListener;

    private final List<EmoticonPageSetEntity> mPageSetEntityList = new ArrayList<>();

    public void setEditText(EditText etChat) {
        mEditText = etChat;
        mEmoticonClickListener = new EmojiClickHandler(mEditText);
    }

    public void addEmojiResProvider(EmojiResProvider provider) {
        EmoticonPageSetEntity entity = provider.getEmoticonPageSetEntity();
        entity.setEmoticonClickListener(mEmoticonClickListener);
        EmoticonFilter filter = provider.getEmoticonFilter(mEditText);
        if (filter != null) {
            mEditText.addTextChangedListener(filter);
        }
        mPageSetEntityList.add(entity);
        EmojiDisplayProcessor processor = provider.getEmojiDisplayProcessor();
        if (processor != null) {
            EmojiDisplay.addEmojiDisplayProcessor(processor);
        }
    }

    public List<EmoticonPageSetEntity> getPageSetEntityList() {
        return mPageSetEntityList;
    }
}
