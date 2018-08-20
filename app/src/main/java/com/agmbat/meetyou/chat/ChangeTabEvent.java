package com.agmbat.meetyou.chat;

public class ChangeTabEvent {

    private int tabIndex;

    public ChangeTabEvent(int tabIndex) {
        this.tabIndex = tabIndex;
    }

    public int getTabIndex(){
        return tabIndex;
    }
}
