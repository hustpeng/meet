
package org.jivesoftware.smackx.paid;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smackx.paid.PaidPageInfoObject.PageItem;
import org.xmlpull.v1.XmlPullParser;

public class PaidPageInfoProvider implements IQProvider {

    public PaidPageInfoProvider() {
    }

    public static String elementName()
    {
        return "query";
    }

    public static String namespace()
    {
        return "http://jabber.org/protocol/vip#privilege";
    }

    public IQ parseIQ(XmlPullParser parser) throws Exception {
        PaidPageInfoPacket packet = new PaidPageInfoPacket();
        PaidPageInfoObject item = new PaidPageInfoObject();

        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG) {
                if ("item".equals(parser.getName())) {
                    item.getSubscriptionPageInfo().add(parseItem(parser));
                }
            }
            else if (eventType == XmlPullParser.END_TAG) {
                if ("query".equals(parser.getName())) {
                    done = true;
                }
            }
        }

        packet.setObject(item);
        return packet;
    }

    private PageItem parseItem(XmlPullParser parser) throws Exception {
        PageItem item = new PageItem();
        item.title = parser.getAttributeValue("", "name");
        item.description = parser.getAttributeValue("", "description");

        return item;
    }
}
