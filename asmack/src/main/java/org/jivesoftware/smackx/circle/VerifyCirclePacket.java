package org.jivesoftware.smackx.circle;

import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.XmppStringUtils;

/**
 * 申请加入群：
 */
public class VerifyCirclePacket extends Packet {

    public VerifyCirclePacket(String circleServer) {
        setTo(circleServer);
    }

    @Override
    public String getXmlns() {
        return "jabber:client";
    }

    /**
     * <iq to="2@circle.yuan520.com"  type = "set" id="sq01">
     * <query xmlns="http://jabber.org/protocol/muc#verify" >
     * <action>applycircle</action>
     * <reason>I want to join you</reason>
     * <sendername>xxxx</sendername>
     * </query>
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
     * * <query xmlns="http://jabber.org/protocol/muc#verify" >
     * <action>applycircle</action>
     * <reason>I want to join you</reason>
     * <sendername>xxxx</sendername>
     * </query>
     *
     * @return
     */
    private String getChildElementXML() {
        StringBuilder buf = new StringBuilder();
        buf.append(" <query xmlns=\"http://jabber.org/protocol/muc#verify\" >");
        buf.append("<action>applycircle</action>");
        buf.append("<reason>I want to join you</reason>");
        buf.append("<sendername>" + getFrom() + "</sendername>");
        buf.append("</query>");
        return buf.toString();
    }

}
