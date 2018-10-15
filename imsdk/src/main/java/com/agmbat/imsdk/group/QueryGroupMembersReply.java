package com.agmbat.imsdk.group;

import org.jivesoftware.smack.packet.IQ;

import java.util.List;

public class QueryGroupMembersReply extends IQ {

    private List<GroupMember> mGroupMembers;

    public List<GroupMember> getGroupMembers() {
        return mGroupMembers;
    }

    public void setGroupMembers(List<GroupMember> groupMembers) {
        mGroupMembers = groupMembers;
    }

    @Override
    public String getChildElementXML() {
        return null;
    }
}
