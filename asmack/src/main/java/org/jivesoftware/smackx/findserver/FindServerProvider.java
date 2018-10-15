package org.jivesoftware.smackx.findserver;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

public class FindServerProvider implements IQProvider {

    public static String elementName() {
        return "query";
    }

    public static String namespace() {
        return "http://jabber.org/protocol/disco#items";
    }

    public IQ parseIQ(XmlPullParser parser) throws Exception {
        FindServerPacket findServerPacket = new FindServerPacket();
        FindServerObject item = new FindServerObject();
        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG) {
                if ("item".equals(parser.getName())) {
                    String name = parser.getAttributeValue("", "name");
                    if ("ticket".equals(name)) {
                        String value = parser.getAttributeValue("", "jid");
                        item.setTokenServer(value);
                    } else if ("circle".equals(name)) {
                        String value = parser.getAttributeValue("", "jid");
                        item.setCircleServer(value);
                    } else if ("vip".equals(name)) {
                        String value = parser.getAttributeValue("", "jid");
                        item.setPaidServer(value);
                    }
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if ("query".equals(parser.getName())) {
                    done = true;
                }
            }
        }
        findServerPacket.setFindServerObject(item);
        return findServerPacket;
    }
}
