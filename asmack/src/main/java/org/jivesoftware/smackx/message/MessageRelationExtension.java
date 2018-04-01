package org.jivesoftware.smackx.message;

import org.jivesoftware.smack.packet.PacketExtension;


public class MessageRelationExtension implements PacketExtension {

    private String type;
    private String action;
    private String entrance;

    public MessageRelationExtension(String action, String type, String entrance) {
        this.type = type;
        this.action = action;
        this.entrance = entrance;
    }

    public MessageRelationExtension(String action, String type) {
        this.type = type;
        this.action = action;
    }

    @Override
    public String getElementName() {
        return MessageRelationProvider.elementName();
    }

    @Override
    public String getNamespace() {
        return MessageRelationProvider.namespace();
    }

    @Override
    public String toXML() {
        if ("visitor".equals(type)) {
            StringBuilder buf = new StringBuilder();
            buf.append("<");
            buf.append(MessageRelationProvider.elementName());
            buf.append(" action=\"");
            buf.append(action);
            buf.append("\" entrance=\"");
            buf.append(entrance);
            buf.append("\" type=\"");
            buf.append(type);
            buf.append("\"/>");
            return buf.toString();
        } else if ("fanme".equals(type)) {
            StringBuilder buf = new StringBuilder();
            buf.append("<");
            buf.append(MessageRelationProvider.elementName());
            buf.append(" action=\"");
            buf.append(action);
            buf.append("\" type=\"");
            buf.append(type);
            buf.append("\"/>");
            return buf.toString();
        } else {
            return null;
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEntrance() {
        return entrance;
    }

    public void setEntrance(String entrance) {
        this.entrance = entrance;
    }

}
