package org.jivesoftware.smackx.findserver;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.util.XmppStringUtils;

public class SendReportPacket extends IQ {

    private String jid;
    private String category;
    private String content;

    public SendReportPacket(String jid, String category, String content) {
        this.jid = jid;
        this.category = category;
        this.content = content;
        setType(Type.SET);
    }

    /**
     * <iq id="HTi3o-11" type="set">
     * <query xmlns="jabber:iq:report">
     * <item jid="aaa\40email.com@10.2.7.139" reportcategory="Fake photo" reportcontent="Fake photo"/>
     * </query>
     * </iq>
     */
    @Override
    public String getChildElementXML() {
        StringBuilder buf = new StringBuilder();
        buf.append("<");
        buf.append("query");
        buf.append(" xmlns=\"");
        buf.append("jabber:iq:report");
        buf.append("\">");
        buf.append("<item jid=\"");
        buf.append(XmppStringUtils.escapeForXML(jid));
        buf.append("\" reportcategory=\"");
        buf.append(category);
        buf.append("\" reportcontent=\"");
        buf.append(content);
        buf.append("\"/></query>");
        buf.toString();
        return buf.toString();
    }

}