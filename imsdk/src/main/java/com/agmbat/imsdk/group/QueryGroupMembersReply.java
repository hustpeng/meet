package com.agmbat.imsdk.group;

import org.jivesoftware.smack.packet.IQ;

import java.util.List;

public class QueryGroupMembersReply extends IQ {

    private List<GroupMember> mGroupMembers;

    public void setGroupMembers(List<GroupMember> groupMembers){
        mGroupMembers = groupMembers;
    }

    public List<GroupMember> getGroupMembers(){
        return mGroupMembers;
    }

    @Override
    public String getChildElementXML() {
        return null;
    }
}
