package org.jivesoftware.smackx.favorites;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;

public class FavoritesProvider implements IQProvider {

    public FavoritesProvider() {
    }

    public static String elementName() {
        return "query";
    }

    public static String namespace() {
        return "jabber:iq:fans";
    }

    public IQ parseIQ(XmlPullParser parser) throws Exception {
        FavoritesPacket favorites = new FavoritesPacket();

        ArrayList<FavoritesObject> favoritesItem = favorites.getFavoritesItems();
        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG) {
                if ("item".equals(parser.getName())) {
                    favoritesItem.add(parseItem(parser));
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if ("query".equals(parser.getName())) {
                    done = true;
                }
            }
        }

        return favorites;
    }

    // Parse the list complex type
    private FavoritesObject parseItem(XmlPullParser parser) throws Exception {
        FavoritesObject item = new FavoritesObject();
        item.setJid(parser.getAttributeValue("", "jid"));
        item.setNickname(parser.getAttributeValue("", "name"));
        item.setAvatar(parser.getAttributeValue("", "avatar"));
        item.setStatus(parser.getAttributeValue("", "status"));
        item.setSubscription(parser.getAttributeValue("", "subscription"));

        String latString = parser.getAttributeValue("", "lat");
        String lonString = parser.getAttributeValue("", "lon");

        try {
            Double latDouble = Double.valueOf(latString);
            item.setLat(latDouble);

            Double lonDouble = Double.valueOf(lonString);
            item.setLon(lonDouble);
        } catch (Exception e) {
            // TODO: handle exception
        }

        return item;
    }
}
