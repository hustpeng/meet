package com.agmbat.meetyou.group;

/**
 * EventBus事件：移除一个群
 */
public class RemoveGroupEvent {

    private String groupJid;

    public RemoveGroupEvent(String groupJid){
        this.groupJid = groupJid;
    }

    public String getGroupJid(){
        return groupJid;
    }
}
