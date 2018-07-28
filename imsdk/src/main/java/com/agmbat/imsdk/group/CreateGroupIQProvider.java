package com.agmbat.imsdk.group;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

public class CreateGroupIQProvider implements IQProvider {

    public static String elementName() {
        return "query";
    }

    public static String namespace() {
        return "http://jabber.org/protocol/muc#owner";
    }

    @Override
    public IQ parseIQ(XmlPullParser parser) throws Exception {
        CreateGroupResultIQ createGroupResultIQ = new CreateGroupResultIQ();
        UpdateGroupReply updateGroupReply = new UpdateGroupReply();
        updateGroupReply.setSuccess(true);
        boolean hasCircle = false;
        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG) {
                if (parser.getName().equals("circle")) {
                    hasCircle = true;
                    String jid = parser.getAttributeValue("", "jid");
                    createGroupResultIQ.setGroupJid(jid);
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if (parser.getName().equals("query")) {
                    done = true;
                }
            }
        }
        if(hasCircle){
            return createGroupResultIQ;
        }else{
            return updateGroupReply;
        }

    }
}
