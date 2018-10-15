package org.jivesoftware.smackx.favoritedme;

import android.net.ParseException;
import android.text.TextUtils;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FavoritedMeProvider implements IQProvider {

    public FavoritedMeProvider() {
    }

    public static String elementName() {
        return "query";
    }

    public static String namespace() {
        return "jabber:iq:fanme";
    }

    public IQ parseIQ(XmlPullParser parser) throws Exception {
        FavoritedMePacket favoritedMe = new FavoritedMePacket();

        ArrayList<FavoritedMeObject> favoritedMeItem = favoritedMe.getFavoritedMeItems();
        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG) {
                if ("item".equals(parser.getName())) {
                    favoritedMeItem.add(parseItem(parser));
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if ("query".equals(parser.getName())) {
                    done = true;
                }
            }
        }

        return favoritedMe;
    }

    // Parse the list complex type
    private FavoritedMeObject parseItem(XmlPullParser parser) throws Exception {
        FavoritedMeObject item = new FavoritedMeObject();
        item.setJid(parser.getAttributeValue("", "jid"));
        item.setNickname(parser.getAttributeValue("", "name"));
        item.setAvatar(parser.getAttributeValue("", "avatar"));
        item.setStatus(parser.getAttributeValue("", "status"));
        item.setSubscription(parser.getAttributeValue("", "subs"));

        String isOnline = parser.getAttributeValue("", "online");
        if ("true".equals(isOnline)) {
            item.setOnline(true);
        } else {
            item.setOnline(false);
        }

        String creationtime = parser.getAttributeValue("", "creationtime");
        if (!TextUtils.isEmpty(creationtime)) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            try {
                Date date = format.parse(creationtime);
                item.setCreate_date(date.getTime());
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

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
