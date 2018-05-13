package org.jivesoftware.smackx.message;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;

import android.text.TextUtils;

public class MessageRelationProvider implements PacketExtensionProvider {

    public MessageRelationProvider() {
    }

    public static String elementName() {
        return "notification";
    }

    public static String namespace() {
        return "jabber:client";
    }

    @Override
    public PacketExtension parseExtension(XmlPullParser parser) throws Exception {
        String action = parser.getAttributeValue("", "action");
        String entrance = parser.getAttributeValue("", "entrance");
        String type = parser.getAttributeValue("", "type");
        if (TextUtils.isEmpty(entrance)) {
            return new MessageRelationExtension(action, type);
        } else {
            return new MessageRelationExtension(action, type, entrance);
        }
    }
}
