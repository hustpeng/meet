/**
 * Copyright (C) 2017 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 * <p>
 * jutils
 *
 * @author mayimchen
 * @since 2017-07-23
 */
package com.agmbat.task;

public interface Method<T> {

    /**
     * 需要执行的方法
     *
     * @return
     */
    T call();
}
