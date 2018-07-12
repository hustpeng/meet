package org.jivesoftware.smackx.circle;

import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.XmppStringUtils;

/**
 * 获取当前用户所属群列表（含当前用户创建的群）
 */
public class CircleListPacket extends Packet {

    public CircleListPacket(String circleServer) {
        setTo(circleServer);
    }

    @Override
    public String getXmlns() {
        return "jabber:client";
    }

    @Override
    public String toXML() {
//        <iq to="circle.yuan520.com" xmlns="jabber:client" id="agsXMPP_13" type="get">
//<query xmlns="http://jabber.org/protocol/disco#items"/>
//</iq>

        StringBuilder buf = new StringBuilder();
        buf.append("<iq ");
        if (getTo() != null) {
            buf.append("to=\"").append(XmppStringUtils.escapeForXML(getTo())).append("\" ");
        }

        if (getXmlns() != null) {
            buf.append(" xmlns=\"").append(getXmlns()).append("\"");
        }

        if (getPacketID() != null) {
            buf.append("id=\"" + getPacketID() + "\" ");
        }

        buf.append("type=\"get\">");

        // Add the query section if there is one.
        String queryXML = getChildElementXML();
        if (queryXML != null) {
            buf.append(queryXML);
        }
        buf.append("</iq>");
        return buf.toString();
    }

    private String getChildElementXML() {
        StringBuilder buf = new StringBuilder();
        buf.append(" <query xmlns=\"http://jabber.org/protocol/disco#items\"/>");
        return buf.toString();
    }
}
