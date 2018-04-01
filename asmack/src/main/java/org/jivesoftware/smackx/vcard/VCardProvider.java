
package org.jivesoftware.smackx.vcard;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

public class VCardProvider implements IQProvider {

    public VCardProvider() {
    }

    public static String elementName()
    {
        return "vCard";
    }

    public static String namespace()
    {
        return "vcard-temp";
    }

    public IQ parseIQ(XmlPullParser parser) throws Exception {
        VCardPacket packet = new VCardPacket();
        VCardObject item = new VCardObject();

        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG) {
                String parserName = parser.getName();

                if ("NICKNAME".equals(parserName)) {
                    item.setNickname(parser.nextText());
                }
                else if ("AVATAR".equals(parserName)) {
                    item.setAvatar(parser.nextText());
                }
                else if ("STATUS".equals(parserName)) {
                    item.setStatus(parser.nextText());
                }
            }
            else if (eventType == XmlPullParser.END_TAG) {
                if ("vCard".equals(parser.getName())) {
                    done = true;
                }
            }
        }

        packet.setObject(item);
        return packet;
    }
}
