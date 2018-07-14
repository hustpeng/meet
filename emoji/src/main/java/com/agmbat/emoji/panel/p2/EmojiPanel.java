package com.agmbat.emoji.panel.p2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.agmbat.emoji.R;
import com.agmbat.emoji.pageset.PageSetAdapter;
import com.agmbat.emoji.pageset.PageSetEntity;

/**
 * 表情面板
 */
public class EmojiPanel extends FrameLayout implements EmoticonsFuncView.OnEmoticonsPageViewListener,
        EmoticonsToolBarView.OnToolBarItemClickListener {

    private EmoticonsFuncView mEmoticonsFuncView;
    private EmoticonsIndicatorView mEmoticonsIndicatorView;
    private EmoticonsToolBarView mEmoticonsToolBarView;

    public EmojiPanel(@NonNull Context context) {
        super(context);
        init();
    }

    public EmojiPanel(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EmojiPanel(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View.inflate(getContext(), R.layout.view_func_emoticon, this);
        mEmoticonsFuncView = (EmoticonsFuncView) findViewById(R.id.view_epv);
        mEmoticonsIndicatorView = (EmoticonsIndicatorView) findViewById(R.id.view_eiv);
        mEmoticonsToolBarView = (EmoticonsToolBarView) findViewById(R.id.view_etv);
        mEmoticonsFuncView.setOnIndicatorListener(this);
        mEmoticonsToolBarView.setOnToolBarItemClickListener(this);
    }

    @Override
    public void emoticonSetChanged(PageSetEntity pageSetEntity) {
        mEmoticonsToolBarView.setToolBtnSelect(pageSetEntity.getUuid());
    }

    @Override
    public void playTo(int position, PageSetEntity pageSetEntity) {
        mEmoticonsIndicatorView.playTo(position, pageSetEntity);
    }

    @Override
    public void playBy(int oldPosition, int newPosition, PageSetEntity pageSetEntity) {
        mEmoticonsIndicatorView.playBy(oldPosition, newPosition, pageSetEntity);
    }

    @Override
    public void onToolBarItemClick(PageSetEntity pageSetEntity) {
        mEmoticonsFuncView.setCurrentPageSet(pageSetEntity);
    }

    public void setConfig(EmojiPanelConfig config) {
        PageSetAdapter pageSetAdapter = new PageSetAdapter();
        for (PageSetEntity entity : config.getPageSetEntityList()) {
            pageSetAdapter.add(entity);
            mEmoticonsToolBarView.addToolItemView(entity);
        }
        mEmoticonsFuncView.setAdapter(pageSetAdapter);
    }

}
