/**
 * Copyright (C) 2016 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 * <p>
 * jutils
 *
 * @author mayimchen
 * @since 2016-10-16
 */
package com.agmbat.server;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 封装分页信息
 */
public class PageModel<E> {

    /**
     * 结果集
     */
    @Expose
    @SerializedName("dataList")
    private List<E> list;

    /**
     * 查询记录数
     */
    @Expose
    @SerializedName("total")
    private int total;

    /**
     * 每页多少条数据
     */
    @Expose
    @SerializedName("pageSize")
    private int pageSize;

    /**
     * 第几页
     */
    @Expose
    @SerializedName("pageNum")
    private int pageNum;

    /**
     * 总页数
     *
     * @return
     */
    public int getTotalPages() {
        // 比较高效的算法，一般是 totalRecords / pageSize + (totalRecords % pageSize == 0 ? 0 : 1)
        return (total + pageSize - 1) / pageSize;
    }

    /**
     * 取得首页
     *
     * @return
     */
    public int getTopPageNum() {
        return 0;
    }

    /**
     * 上一页
     *
     * @return
     */
    public int getPreviousPageNum() {
        int topPageNum = getTopPageNum();
        if (pageNum <= topPageNum) {
            return topPageNum;
        }
        return pageNum - 1;
    }

    /**
     * 下一页
     *
     * @return
     */
    public int getNextPageNum() {
        int bottomPageNum = getBottomPageNum();
        if (pageNum >= bottomPageNum) {
            return bottomPageNum;
        }
        return pageNum + 1;
    }

    /**
     * 取得尾页
     *
     * @return
     */
    public int getBottomPageNum() {
        return getTotalPages() - 1;
    }

    public List<E> getList() {
        return list;
    }

    public void setList(List<E> list) {
        this.list = list;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }
}
