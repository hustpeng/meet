package com.agmbat.imsdk.group;

import android.net.ParseException;
import android.text.TextUtils;

import com.agmbat.imsdk.asmack.XMPPManager;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.DateFormatType;
import org.jivesoftware.smack.util.XmppStringUtils;
import org.jivesoftware.smackx.message.MessageObject;
import org.jivesoftware.smackx.message.MessageObjectStatus;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GroupChatIQProvider implements IQProvider {

    public static String elementName() {
        return "query";
    }

    public static String namespace() {
        return "http://jabber.org/protocol/muc#circlehistory";
    }

    @Override
    public IQ parseIQ(XmlPullParser parser) throws Exception {
        GroupChatReply groupChatReply = new GroupChatReply();
        MessageObject messageObject = null;
        String groupJid = "";
        String myJid = XMPPManager.getInstance().getXmppConnection().getBareJid();
        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG) {
                if (parser.getName().equals("msg")) {
                    messageObject = new MessageObject();
                    String type = parser.getAttributeValue("", "type");
                    if ("groupchat".equals(type)) {
                        messageObject.setChatType(Message.Type.groupchat);
                    }
                    groupJid = XmppStringUtils.parseBareAddress(parser.getAttributeValue("", "from"));
                    String msgId = parser.getAttributeValue("", "id");
                    messageObject.setAccount(XMPPManager.getInstance().getXmppConnection().getBareJid());
                    messageObject.setMsgId(msgId);
                    groupChatReply.addMessage(messageObject);
                } else if (parser.getName().equals("body")) {
                    messageObject.setBody(parseContent(parser));
                } else if (parser.getName().equals("sender")) {
                    String senderJid = XmppStringUtils.parseBareAddress(parser.nextText());
                    messageObject.setSenderJid(senderJid);
                    if (myJid.equals(senderJid)) {
                        messageObject.setOutgoing(true);
                        messageObject.setMsgStatus(MessageObjectStatus.SEND);
                        messageObject.setFromJid(myJid);
                        messageObject.setToJid(groupJid);
                    } else {
                        messageObject.setOutgoing(false);
                        messageObject.setMsgStatus(MessageObjectStatus.UNREAD);
                        messageObject.setFromJid(groupJid);
                        messageObject.setToJid(myJid);
                    }

                } else if (parser.getName().equals("delay")) {
                    Date date = parseOfflineMessageDate(parser);
                    if (date != null) {
                        messageObject.setDate(date.getTime());
                    }
                } else if (parser.getName().equals("nick")) {
                    messageObject.setSenderNickName(parser.nextText());
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if (parser.getName().equals("messages")) {
                    done = true;
                }
            }
        }
        return groupChatReply;
    }

    private static String parseContent(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        String content = "";
        int parserDepth = parser.getDepth();
        while (!(parser.next() == XmlPullParser.END_TAG && parser.getDepth() == parserDepth)) {
            content += parser.getText();
        }
        return content;
    }

    private static Date parseOfflineMessageDate(XmlPullParser parser) {
        String stampString = parser.getAttributeValue("", "stamp");
        if (!TextUtils.isEmpty(stampString)) {
            SimpleDateFormat format = DateFormatType.XEP_0082_DATETIME_MILLIS_PROFILE.createFormatter();
            try {
                while (stampString.startsWith("0")) {
                    stampString = stampString.substring(1, stampString.length());
                }
                Date date = format.parse(stampString);
                return date;
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
