package com.agmbat.imsdk.group;

import android.text.TextUtils;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class JoinGroupProvider implements IQProvider {

    public static String elementName() {
        return "query";
    }

    public static String namespace() {
        return "http://jabber.org/protocol/muc#verify";
    }

    @Override
    public IQ parseIQ(XmlPullParser parser) throws Exception {
        boolean done = false;
        String action = "";
        JoinGroupReply joinGroupReply = new JoinGroupReply();
        while (!done) {
            int eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG) {
                if (parser.getName().equals("action")) {
                    action = geTagText(parser,"action");
                }else if(parser.getName().equals("succeed")){
                    if("applycircle".equals(action)){
                        String succeedText = geTagText(parser,"succeed");
                        boolean success = Boolean.parseBoolean(succeedText);
                        joinGroupReply.setSuccess(success);
                    }
                }else if(parser.getName().equals("wait")){
                    if("applycircle".equals(action)){
                        String waitText = geTagText(parser, "wait");
                        boolean wait = Boolean.parseBoolean(waitText);
                        joinGroupReply.setWaitForAgree(wait);
                    }
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if (parser.getName().equals("query")) {
                    done = true;
                }
            }
        }
        return joinGroupReply;
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
