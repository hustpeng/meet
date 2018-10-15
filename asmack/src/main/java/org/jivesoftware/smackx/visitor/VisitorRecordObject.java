package org.jivesoftware.smackx.visitor;

public class VisitorRecordObject {

    private String whoVisitorJid;
    private String visitorWhoJid;
    private String entrance;
    private long visitorTime;

    public static String getXmlNode(VisitorRecordObject object) {
        if (object == null) {
            return null;
        }

        StringBuilder buf = new StringBuilder();
        buf.append("<item jid=\"");
        buf.append(object.getVisitorWhoJid());
//        buf.append("\" entrnce=\"");
        buf.append("\" entrance=\"");
        buf.append(object.getEntrance());
        buf.append("\" accesstime=\"");
        buf.append(object.getVisitorTime() / 1000);
        buf.append("\"/>");

        return buf.toString();
    }

    public String getEntrance() {
        return entrance;
    }

    public void setEntrance(String entrance) {
        this.entrance = entrance;
    }

    public long getVisitorTime() {
        return visitorTime;
    }

    public void setVisitorTime(long visitorTime) {
        this.visitorTime = visitorTime;
    }

    public String getWhoVisitorJid() {
        return whoVisitorJid;
    }

    public void setWhoVisitorJid(String whoVisitorJid) {
        this.whoVisitorJid = whoVisitorJid;
    }

    public String getVisitorWhoJid() {
        return visitorWhoJid;
    }

    public void setVisitorWhoJid(String visitorWhoJid) {
        this.visitorWhoJid = visitorWhoJid;
    }

    @Override
    public String toString() {
        return getXmlNode(this);
    }
}