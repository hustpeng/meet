package com.agmbat.imsdk.group;

import org.jivesoftware.smack.packet.IQ;

import java.util.List;

public class QueryGroupResultIQ extends IQ {


    private List<GroupBean> groups;

    public List<GroupBean> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupBean> groups) {
        this.groups = groups;
    }

    @Override
    public String getChildElementXML() {
        StringBuilder builder = new StringBuilder();
        builder.append("<query xmlns=\"" + QueryGroupIQProvider.namespace() + "\">");
        if (null != groups) {
            for (int i = 0; i < groups.size(); i++) {
                GroupBean groupBean = groups.get(i);
                builder.append("<item jid=\"" + groupBean.getGroupJid() + "\" members=\"" + groupBean.getMembers() + "\" name=\"" + groupBean.getName() + "\" cover=\"" + groupBean.getAvatar() + "\" owner=\"" + groupBean.getOwnerJid() + "\" circle_status=\"approved\"/>");
            }
        }
        builder.append("</query>");
        return builder.toString();
    }

}
