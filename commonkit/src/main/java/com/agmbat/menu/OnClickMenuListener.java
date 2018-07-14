package com.agmbat.menu;

/**
 * 点击Menu的事件回调
 */
public interface OnClickMenuListener {

    /**
     * 点击menu
     *
     * @param menu
     * @param index
     */
    public void onClick(MenuInfo menu, int index);

}