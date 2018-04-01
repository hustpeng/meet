package org.jivesoftware.smackx.permit;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;

public class PermitProvider implements IQProvider {

    public PermitProvider() {

    }

    public static String elementName() {
        return "query";
    }

    public static String namespace() {
        return "jabber:iq:permit";
    }

    @Override
    public IQ parseIQ(XmlPullParser parser) throws Exception {
        PermitPacket permit = new PermitPacket();
        ArrayList<PermitObject> permitItem = permit.getPermitItems();

        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG) {
                if ("item".equals(parser.getName())) {
                    permitItem.add(parseItem(parser));
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if ("query".equals(parser.getName())) {
                    done = true;
                }
            }
        }

        return permit;
    }

    private PermitObject parseItem(XmlPullParser parser) throws Exception {
        PermitObject item = new PermitObject();
        item.setJid(parser.getAttributeValue("", "jid"));
        item.setNickname(parser.getAttributeValue("", "nickname"));
        item.setAvatar(parser.getAttributeValue("", "avatar"));
        item.setStatus(parser.getAttributeValue("", "status"));
        item.setAction(parser.getAttributeValue("", "action"));
        return item;

    }
}
