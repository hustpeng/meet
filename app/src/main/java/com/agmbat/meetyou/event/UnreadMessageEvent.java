package com.agmbat.meetyou.event;

public class UnreadMessageEvent {

    private boolean hasUnread;

    public UnreadMessageEvent(boolean hasUnread) {
        this.hasUnread = hasUnread;
    }

    public boolean hasUnread() {
        return hasUnread;
    }
}
