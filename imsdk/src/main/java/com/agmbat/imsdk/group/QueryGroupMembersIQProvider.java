package com.agmbat.imsdk.group;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.List;

public class QueryGroupMembersIQProvider implements IQProvider {

    public static String elementName() {
        return "query";
    }

    public static String namespace() {
        return "http://jabber.org/protocol/muc#circlemembers";
    }

    @Override
    public IQ parseIQ(XmlPullParser parser) throws Exception {
        boolean done = false;
        List<GroupMember> groupMembers = new ArrayList<>();
        while (!done) {
            int eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG) {
                if (parser.getName().equals("item")) {
                    GroupMember groupMember = new GroupMember();
                    groupMember.setNickName(parser.getAttributeValue("", "nickname"));
                    groupMember.setAvatar(parser.getAttributeValue("", "avatar"));
                    groupMember.setJid(parser.getAttributeValue("", "jid"));
                    groupMember.setRole(parser.getAttributeValue("", "role"));
                    groupMembers.add(groupMember);
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if (parser.getName().equals("query")) {
                    done = true;
                }
            }
        }
        QueryGroupMembersReply queryGroupMembersResultIQ = new QueryGroupMembersReply();
        queryGroupMembersResultIQ.setGroupMembers(groupMembers);
        return queryGroupMembersResultIQ;
    }
}
