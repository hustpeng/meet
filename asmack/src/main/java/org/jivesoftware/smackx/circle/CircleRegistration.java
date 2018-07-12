package org.jivesoftware.smackx.circle;

import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.XmppStringUtils;

/**
 * 获取注册群的表单
 */
public class CircleRegistration extends Packet {

    public CircleRegistration(String circleServer) {
        setTo(circleServer);
    }

    @Override
    public String getXmlns() {
        return "jabber:client";
    }

    /**
     * <iq to="circle.yuan520.com" id="sd18" type="get" xmlns="jabber:client">
     * <query xml:lang="en" xmlns="http://jabber.org/protocol/muc#owner"/>
     * </iq>
     *
     * @return
     */
    @Override
    public String toXML() {
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
        buf.append(" <query xml:lang=\"en\" xmlns=\"http://jabber.org/protocol/muc#owner\"/>");
        return buf.toString();
    }

}
