package org.jivesoftware.smackx.token;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

public class TokenProvider implements IQProvider {

    public TokenProvider() {
    }

    public static String elementName() {
        return "query";
    }

    public static String namespace() {
        return "jabber:iq:ticket";
    }

    public IQ parseIQ(XmlPullParser parser) throws Exception {
        TokenPacket packet = new TokenPacket();
        TokenObject item = new TokenObject();

        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG) {
                if ("key".equals(parser.getName())) {
                    item.setToken(parser.nextText());
                } else if ("expiretime".equals(parser.getName())) {
                    String expirdString = parser.nextText();
                    try {
                        Long expirdTime = Long.valueOf(expirdString);
                        item.setExpirdTime(expirdTime);
                    } catch (NumberFormatException e) {
                        item.setExpirdTime(0);
                    }
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if ("query".equals(parser.getName())) {
                    done = true;
                }
            }
        }

        packet.setObject(item);
        return packet;
    }
}
