/**
 * Copyright (C) 2017 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 * <p>
 * jutils
 *
 * @author mayimchen
 * @since 2017-07-23
 */
package com.agmbat.task;

/**
 * 等待
 */
public interface Waitable {

    /**
     * 是否还需要等待
     *
     * @return
     */
    public boolean canWait();
}
