package com.agmbat.pagedataloader;

import java.util.List;

/**
 * 描述请求的数据模型
 *
 * @param <T>
 */
public interface PageData<T> {

    /**
     * 是否成功
     *
     * @return
     */
    public boolean isSuccess();

    /**
     * 当前请求的数据
     *
     * @return
     */
    public List<T> getDataList();

    /**
     * 当前page num
     *
     * @return
     */
    public int getPageNum();

    /**
     * 是否还有下一页数据
     *
     * @return
     */
    public boolean hasNextPageData();
}
