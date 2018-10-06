package com.agmbat.imsdk.group;

import com.agmbat.text.StringUtils;

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
        QuitGroupReplay quitGroupReplay = new QuitGroupReplay();
        DismissGroupReply dismissGroupReply = new DismissGroupReply();
        ChangeGroupNicknameReply changeGroupNicknameReply = new ChangeGroupNicknameReply();
        while (!done) {
            int eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG) {
                if (parser.getName().equals("action")) {
                    action = geTagText(parser, "action");
                } else if (parser.getName().equals("succeed")) {
                    String succeedText = geTagText(parser, "succeed");
                    boolean success = StringUtils.parseBoolean(succeedText);
                    if ("applycircle".equals(action)) {
                        joinGroupReply.setSuccess(success);
                    } else if ("quitcircle".equals(action)) {
                        quitGroupReplay.setSuccess(success);
                    } else if ("dismisscircle".equals(action)) {
                        dismissGroupReply.setSuccess(success);
                    }else if("changenickname".equals(action)){
                        changeGroupNicknameReply.setSuccess(success);
                    }
                } else if (parser.getName().equals("wait")) {
                    if ("applycircle".equals(action)) {
                        String waitText = geTagText(parser, "wait");
                        boolean wait = Boolean.parseBoolean(waitText);
                        joinGroupReply.setWaitForAgree(wait);
                    }
                } else if (parser.getName().equals("reason")) {
                    String reason = geTagText(parser, "reason");
                    if ("quitcircle".equals(action)) {
                        quitGroupReplay.setReason(reason);
                    } else if ("dismisscircle".equals(action)) {
                        dismissGroupReply.setReason(reason);
                    }
                }else if(parser.getName().equals("sendername")){
                    if("changenickname".equals(action)){
                        changeGroupNicknameReply.setGroupNickname(parser.nextText());
                    }
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if (parser.getName().equals("query")) {
                    done = true;
                }
            }
        }
        if ("applycircle".equals(action)) {
            return joinGroupReply;
        } else if ("quitcircle".equals(action)) {
            return quitGroupReplay;
        } else if ("dismisscircle".equals(action)) {
            return dismissGroupReply;
        } else if ("changenickname".equals(action)) {
            return changeGroupNicknameReply;
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
