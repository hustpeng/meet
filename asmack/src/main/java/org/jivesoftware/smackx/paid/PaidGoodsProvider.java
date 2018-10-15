package org.jivesoftware.smackx.paid;

import android.net.ParseException;
import android.text.TextUtils;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PaidGoodsProvider implements IQProvider {

    public PaidGoodsProvider() {
    }

    public static String elementName() {
        return "query";
    }

    public static String namespace() {
        return "http://jabber.org/protocol/vip#goodslist";
    }

    public IQ parseIQ(XmlPullParser parser) throws Exception {
        PaidAccountPacket packet = new PaidAccountPacket();
        PaidAccountObject item = new PaidAccountObject();

        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG) {
                String parserName = parser.getName();

                if ("coins".equals(parserName)) {
                    item.setCoins(Integer.valueOf(parser.getAttributeValue("", "balance")));
                } else if ("subcription".equals(parserName)) {
                    item.setSubscriptionVaild(!"true".equals(parser.getAttributeValue("", "expired")));

                    String subscriptiontime = parser.getAttributeValue("", "expireddate");
                    if (!TextUtils.isEmpty(subscriptiontime)) {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                        try {
                            Date date = format.parse(subscriptiontime);
                            item.setSubscriptionTime(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if (elementName().equals(parser.getName())) {
                    done = true;
                }
            }
        }

        packet.setObject(item);
        return packet;
    }
}
