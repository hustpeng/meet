package com.agmbat.emoji.panel.p2;

import com.agmbat.emoji.DelBtnStatus;
import com.agmbat.emoji.EmojiBean;
import com.agmbat.emoji.pageset.PageSetEntity;
import com.agmbat.emoji.panel.EmoticonClickListener;
import com.agmbat.emoji.panel.p2.page.GridAdapterFactory;

import java.util.List;

/**
 * 表示一大类表情, 可以分成多个pager页面
 */
public class EmoticonPageSetEntity extends PageSetEntity<EmoticonPageEntity> {

    /**
     * 每页行数
     */
    private int mLine;

    /**
     * 每页列数
     */
    private int mRow;

    /**
     * 删除按钮
     */
    private DelBtnStatus mDelBtnStatus = DelBtnStatus.GONE;

    /**
     * 表情集数据源
     */
    private List<EmojiBean> mEmoticonList;

    /**
     * adapter配置
     */
    private GridAdapterFactory mGridAdapterFactory;

    public void setGridAdapterFactory(GridAdapterFactory factory) {
        mGridAdapterFactory = factory;
    }

    public int getLine() {
        return mLine;
    }

    public int getRow() {
        return mRow;
    }

    public DelBtnStatus getDelBtnStatus() {
        return mDelBtnStatus;
    }

    public List<EmojiBean> getEmoticonList() {
        return mEmoticonList;
    }

    public void setLine(int line) {
        mLine = line;
    }

    public void setRow(int row) {
        mRow = row;
    }

    public void setShowDelBtn(DelBtnStatus showDelBtn) {
        mDelBtnStatus = showDelBtn;
    }

    public void setEmoticonList(List<EmojiBean> emoticonList) {
        mEmoticonList = emoticonList;
    }

    /**
     * 准备 PageEntityList
     */
    public void preparePageEntity() {
        int emoticonSetSum = mEmoticonList.size();
        int del = mDelBtnStatus.isShow() ? 1 : 0;
        int everyPageMaxSum = mRow * mLine - del;
        mPageCount = (int) Math.ceil((double) mEmoticonList.size() / everyPageMaxSum);

        int start = 0;
        int end = everyPageMaxSum > emoticonSetSum ? emoticonSetSum : everyPageMaxSum;

        if (!mPageEntityList.isEmpty()) {
            mPageEntityList.clear();
        }

        for (int i = 0; i < mPageCount; i++) {
            EmoticonPageEntity emoticonPageEntity = new EmoticonPageEntity();
            emoticonPageEntity.setLine(mLine);
            emoticonPageEntity.setRow(mRow);
            emoticonPageEntity.setDelBtnStatus(mDelBtnStatus);
            emoticonPageEntity.setEmoticonList(mEmoticonList.subList(start, end));
            emoticonPageEntity.setGridAdapterFactory(mGridAdapterFactory);
            mPageEntityList.add(emoticonPageEntity);

            start = everyPageMaxSum + i * everyPageMaxSum;
            end = everyPageMaxSum + (i + 1) * everyPageMaxSum;
            if (end >= emoticonSetSum) {
                end = emoticonSetSum;
            }
        }
    }

    public void setEmoticonClickListener(EmoticonClickListener emojiClickHandler) {
        for (EmoticonPageEntity entity : mPageEntityList) {
            entity.setEmoticonClickListener(emojiClickHandler);
        }
    }
}
