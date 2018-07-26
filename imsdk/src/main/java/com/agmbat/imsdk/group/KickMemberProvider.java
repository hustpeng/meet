package com.agmbat.imsdk.group;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class KickMemberProvider implements IQProvider {

    public static String elementName() {
        return "query";
    }

    public static String namespace() {
        return "http://jabber.org/protocol/muc#admin";
    }

    @Override
    public IQ parseIQ(XmlPullParser parser) throws Exception {
        boolean done = false;
        KickMemberReply kickMemberReply = new KickMemberReply();
        TransOwnerReply transOwnerReply = new TransOwnerReply();
        String action = "";
        while (!done) {
            int eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG) {
                if (parser.getName().equals("member")) {
                    kickMemberReply.setMember(geTagText(parser, "member"));
                } else if (parser.getName().equals("action")) {
                    action = geTagText(parser, "action");
                    kickMemberReply.setAction(action);
                    transOwnerReply.setAction(action);
                } else if (parser.getName().equals("reason")) {
                    kickMemberReply.setReason(geTagText(parser, "reason"));
                } else if (parser.getName().equals("sendername")) {
                    kickMemberReply.setSender(geTagText(parser, "sendername"));
                } else if (parser.getName().equals("new_owner")) {
                    transOwnerReply.setNewOwner(geTagText(parser, "new_owner"));
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if (parser.getName().equals("query")) {
                    done = true;
                }
            }
        }
        if (action.equals("transowner")) {
            return transOwnerReply;
        } else if (action.equals("kickmember")) {
            return kickMemberReply;
        }
        return null;
    }


    private String geTagText(XmlPullParser xmlPullParser, String tagName)
            throws XmlPullParserException, IOException {
        String mCity = "";
        xmlPullParser.require(XmlPullParser.START_TAG, null, tagName);
        if (xmlPullParser.next() == XmlPullParser.TEXT) {
            mCity = xmlPullParser.getText();
            xmlPullParser.nextTag();
        }
        xmlPullParser.require(XmlPullParser.END_TAG, null, tagName);
        return mCity;
    }
}
