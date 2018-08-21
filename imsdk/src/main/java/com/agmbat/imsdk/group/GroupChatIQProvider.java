package com.agmbat.imsdk.group;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

public class GroupChatIQProvider implements IQProvider {

    public static String elementName() {
        return "query";
    }

    public static String namespace() {
        return "http://jabber.org/protocol/muc#circlehistory";
    }

    @Override
    public IQ parseIQ(XmlPullParser parser) throws Exception {
        return null;
    }
}
