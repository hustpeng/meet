package com.agmbat.emoji;

/**
 * 描述删除Button的状态
 */
public enum DelBtnStatus {
    // 0,1,2
    GONE, FOLLOW, LAST;

    public boolean isShow() {
        return !GONE.toString().equals(this.toString());
    }
}