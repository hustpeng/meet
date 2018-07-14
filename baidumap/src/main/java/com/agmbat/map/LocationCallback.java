package com.agmbat.map;

/**
 * 位置信息回调
 */
public interface LocationCallback {

    /**
     * 返回位置信息
     *
     * @param location
     */
    public void callback(LocationObject location);
}
