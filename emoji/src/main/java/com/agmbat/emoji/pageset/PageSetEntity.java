package com.agmbat.emoji.pageset;

import java.util.LinkedList;
import java.util.UUID;

/**
 * 单个类型, 支持多个pager
 *
 * @param <T>
 */
public class PageSetEntity<T extends PageEntity> {

    protected final String uuid = UUID.randomUUID().toString();

    /**
     * page个数
     */
    protected int mPageCount;

    /**
     * 是否显示Indicator
     */
    protected boolean mIsShowIndicator = true;

    /**
     * 子Pager
     */
    protected LinkedList<T> mPageEntityList = new LinkedList<>();

    /**
     * icon
     */
    protected String mIconUri;

    /**
     * 名称
     */
    protected String mSetName;


    public String getIconUri() {
        return mIconUri;
    }

    public void setIconUri(String iconUri) {
        mIconUri = iconUri;
    }

    public int getPageCount() {
        return mPageEntityList == null ? 0 : mPageEntityList.size();
    }

    public void setPageCount(int pageCount) {
        mPageCount = pageCount;
    }

    public LinkedList<T> getPageEntityList() {
        return mPageEntityList;
    }

    public void setPageEntityList(LinkedList<T> pageEntityList) {
        mPageEntityList = pageEntityList;
    }

    public void addPageEntity(T pageEntityt) {
        mPageEntityList.add(pageEntityt);
    }

    public String getUuid() {
        return uuid;
    }

    public boolean isShowIndicator() {
        return mIsShowIndicator;
    }

    public void setShowIndicator(boolean showIndicator) {
        mIsShowIndicator = showIndicator;
    }

    public void setSetName(String setName) {
        mSetName = setName;
    }


}
