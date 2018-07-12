package org.jivesoftware.smackx.circle;

import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.XmppStringUtils;

/**
 * 群成员退出群
 */
public class ExitCirclePacket extends Packet {

    public ExitCirclePacket(String circleServer) {
        setTo(circleServer);
    }

    @Override
    public String getXmlns() {
        return "jabber:client";
    }

    /**
     * <iq to="2@circle.yuan520.com" type = "set" id="cr01">
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

        buf.append("type=\"set\">");

        // Add the query section if there is one.
        String queryXML = getChildElementXML();
        if (queryXML != null) {
            buf.append(queryXML);
        }
        buf.append("</iq>");
        return buf.toString();
    }

    /**
     * * <query xmlns="http://jabber.org/protocol/muc#verify">
     * <action>quitcircle</actoin>
     * <sendername>xxxx</sendername>
     * </query>
     *
     * @return
     */
    private String getChildElementXML() {
        StringBuilder buf = new StringBuilder();
        buf.append(" <query xmlns=\"http://jabber.org/protocol/muc#verify\">");
        buf.append("<action>quitcircle</actoin>");
        buf.append(" <sendername>" + getFrom() + "</sendername>");
        buf.append("</query>");
        return buf.toString();
    }

}
