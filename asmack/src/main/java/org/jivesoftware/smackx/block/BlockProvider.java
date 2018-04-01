package org.jivesoftware.smackx.block;

import java.util.ArrayList;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

public class BlockProvider implements IQProvider {
    public BlockProvider() {

    }

    public static String elementName() {
        return "query";
    }

    public static String namespace() {
        return "jabber:iq:privacy";
    }

    @Override
    public IQ parseIQ(XmlPullParser parser) throws Exception {
        BlockPacket permit = new BlockPacket();
        ArrayList<BlockObject> permitItem = permit.getBlockItems();
        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG) {
                if ("item".equals(parser.getName())) {
                    permitItem.add(parseItem(parser));
                } else if ("list".equals(parser.getName())) {
                    permit.setListName(ParseListName(parser));
                } else if ("default".equals(parser.getName())) {
                    permit.setDefaultName(ParseListName(parser));
                } else if ("active".equals(parser.getName())) {
                    permit.setActiveName(ParseListName(parser));
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if ("query".equals(parser.getName())) {
                    done = true;
                }
            }
        }
        return permit;
    }

    private BlockObject parseItem(XmlPullParser parser) throws Exception {
        BlockObject item = new BlockObject();
        item.setJid(parser.getAttributeValue("", "value"));
        return item;
    }

    private String ParseListName(XmlPullParser parser) throws Exception {
        String listName = "";
        listName = parser.getAttributeValue("", "name");
        return listName;
    }
}
