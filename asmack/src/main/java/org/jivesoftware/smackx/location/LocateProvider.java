
package org.jivesoftware.smackx.location;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

public class LocateProvider implements IQProvider {

    public LocateProvider() {
    }

    public static String elementName()
    {
        return "geoloc";
    }

    public static String namespace()
    {
        return "http://jabber.org/protocol/geoloc";
    }

    public IQ parseIQ(XmlPullParser parser) throws Exception {
        LocatePacket packet = new LocatePacket();
        LocateObject item = new LocateObject();

        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG) {
                if ("lat".equals(parser.getName())) {
                    String latStr = parser.nextText();
                    try {
                        Double latDouble = Double.valueOf(latStr);
                        item.setLat(latDouble);
                    } catch (Exception e) {
                        item.setLat(0);
                    }
                }
                else if ("lon".equals(parser.getName())) {
                    String lonStr = parser.nextText();
                    try {
                        Double lonDouble = Double.valueOf(lonStr);
                        item.setLon(lonDouble);
                    } catch (Exception e) {
                        item.setLon(0);
                    }
                }
                else if ("str".equals(parser.getName())) {
                    item.setLocationStr(parser.nextText());
                }
            }
            else if (eventType == XmlPullParser.END_TAG) {
                if ("geoloc".equals(parser.getName())) {
                    done = true;
                }
            }
        }

        packet.setObject(item);
        return packet;
    }
}
