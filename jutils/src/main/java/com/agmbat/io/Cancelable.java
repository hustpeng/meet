package com.agmbat.io;

/**
 * 可以取消的操作
 */
public interface Cancelable {

    /**
     * 是否已经取消
     *
     * @return true 已经取消
     */
    public boolean isCancelled();
}
