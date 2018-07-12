package org.jivesoftware.smackx.circle;

import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.XmppStringUtils;

/**
 * 踢群成员
 */
public class KickMemberPacket extends Packet {

    /**
     * <member>15571767415@yuan520.com</member>
     */
    private String member;
    private String reason;

    public KickMemberPacket(String circleServer) {
        setTo(circleServer);
    }

    @Override
    public String getXmlns() {
        return "jabber:client";
    }

    /**
     * <iq to="2@circle.yuan520.com" type = "set" id="adf1234">
     * <query xmlns="http://jabber.org/protocol/muc#admin" >
     * <action>kickmember</action>
     * <member>15571767415@yuan520.com</member>
     * <reason>XXXXX</reason>
     * <sendername>xxxxx</sendername>
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
     * <query xmlns="http://jabber.org/protocol/muc#admin" >
     * <action>kickmember</action>
     * <member>15571767415@yuan520.com</member>
     * <reason>XXXXX</reason>
     * <sendername>xxxxx</sendername>
     * </query>
     *
     * @return
     */
    private String getChildElementXML() {
        StringBuilder buf = new StringBuilder();
        buf.append(" <query xmlns=\"http://jabber.org/protocol/muc#admin\" >");
        buf.append("<action>kickmember</action>");
        buf.append("<member>" + member + "</member>");
        buf.append("<reason>" + reason + "</reason>");
        buf.append("<sendername>" + getFrom() + "</sendername>");
        buf.append("</query>");
        return buf.toString();
    }

}
