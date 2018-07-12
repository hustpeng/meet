package org.jivesoftware.smackx.circle;

import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.XmppStringUtils;

/**
 * 修改群配置信息表单(只有群主才能获取修改群配置form)
 * <iq to="2@circle.yuan520.com" id="sd18" type="get" xmlns="jabber:client">
 * <query xml:lang="en" xmlns="http://jabber.org/protocol/muc#owner"/>
 * </iq>
 */
public class UpdateCircleInfoPacket extends Packet {

    public UpdateCircleInfoPacket(String to) {
        setTo(to);
    }

    @Override
    public String getXmlns() {
        return "jabber:client";
    }

    /**
     * <iq to="3@circle.yuan520.com" xmlns="jabber:client" id="agsXMPP_13" type="get">
     * <query xmlns="http://jabber.org/protocol/disco#items"/>
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
