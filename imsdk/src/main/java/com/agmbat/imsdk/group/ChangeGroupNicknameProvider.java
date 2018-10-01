package com.agmbat.imsdk.group;

import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.text.StringUtils;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

public class ChangeGroupNicknameProvider implements IQProvider {

    public static String elementName() {
        return "query";
    }

    public static String namespace() {
        return "http://jabber.org/protocol/muc#verify";
    }


    @Override
    public IQ parseIQ(XmlPullParser parser) throws Exception {
        ChangeGroupNicknameReply reply = new ChangeGroupNicknameReply();
        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG) {
                if (parser.getName().equals("succeed")) {
                    String succeedValue = parser.nextText();
                    reply.setSuccess(StringUtils.parseBoolean(succeedValue));
                } else if (parser.getName().equals("sendername")) {
                    reply.setGroupNickname(parser.nextText());
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if (parser.getName().equals("query")) {
                    done = true;
                }
            }
        }
        return reply;
    }
}
