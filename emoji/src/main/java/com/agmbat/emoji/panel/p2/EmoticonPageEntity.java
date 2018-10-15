package com.agmbat.emoji.panel.p2;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.agmbat.emoji.DelBtnStatus;
import com.agmbat.emoji.EmojiBean;
import com.agmbat.emoji.pageset.PageEntity;
import com.agmbat.emoji.panel.EmoticonClickListener;
import com.agmbat.emoji.panel.p2.page.EmoticonPageView;
import com.agmbat.emoji.panel.p2.page.GridAdapterFactory;

import java.util.List;

public class EmoticonPageEntity extends PageEntity<EmoticonPageEntity> {

    /**
     * 表情数据源
     */
    private List<EmojiBean> mEmoticonList;

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
    private DelBtnStatus mDelBtnStatus;

    /**
     * adapter配置
     */
    private GridAdapterFactory mGridAdapterFactory;

    private EmoticonClickListener mEmoticonClickListener;

    public EmoticonPageEntity() {
    }

    public EmoticonPageEntity(View view) {
        super(view);
    }


    public void setEmoticonClickListener(EmoticonClickListener l) {
        mEmoticonClickListener = l;
    }

    public GridAdapterFactory getGridAdapterFactory() {
        return mGridAdapterFactory;
    }

    public void setGridAdapterFactory(GridAdapterFactory factory) {
        mGridAdapterFactory = factory;
    }

    public List<EmojiBean> getEmoticonList() {
        return mEmoticonList;
    }

    public void setEmoticonList(List<EmojiBean> emoticonList) {
        mEmoticonList = emoticonList;
    }

    public int getLine() {
        return mLine;
    }

    public void setLine(int line) {
        this.mLine = line;
    }

    public int getRow() {
        return mRow;
    }

    public void setRow(int row) {
        this.mRow = row;
    }

    public DelBtnStatus getDelBtnStatus() {
        return mDelBtnStatus;
    }

    public void setDelBtnStatus(DelBtnStatus delBtnStatus) {
        this.mDelBtnStatus = delBtnStatus;
    }

    @Override
    public View instantiateItem(final ViewGroup container, int position) {
        if (getRootView() == null) {
            EmoticonPageView pageView = new EmoticonPageView(container.getContext());
            pageView.setNumColumns(mRow);
            BaseAdapter adapter = mGridAdapterFactory.create(pageView.getContext(), this, mEmoticonClickListener);
            pageView.setAdapter(adapter);
            setRootView(pageView);
        }
        return getRootView();
    }

}
