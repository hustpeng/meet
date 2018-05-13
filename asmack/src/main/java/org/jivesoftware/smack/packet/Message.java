/**
 * $RCSfile$
 * $Revision$
 * $Date$
 * <p>
 * Copyright 2003-2007 Jive Software.
 * <p>
 * All rights reserved. Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jivesoftware.smack.packet;

import org.jivesoftware.smack.util.XmppStringUtils;

import java.util.Date;

public class Message extends Packet {

    private Type type = Type.normal;
    private MessageSubType subType;
    private String senderNickName = null;
    private boolean isOffline = false;
    private String body = null;
    private Date date = null;

    /**
     * Creates a new, "normal" message.
     */
    public Message() {
    }

    /**
     * Creates a new "normal" message to the specified recipient.
     *
     * @param to the recipient of the message.
     */
    public Message(String to) {
        setTo(to);
    }

    /**
     * Creates a new message of the specified type to a recipient.
     *
     * @param to   the user to send the message to.
     * @param type the message type.
     */
    public Message(String to, Type type) {
        setTo(to);
        this.type = type;
    }

    /**
     * Returns the type of the message. If no type has been set this method will return {@link
     * Type#normal}.
     *
     * @return the type of the message.
     */
    public Type getType() {
        return type;
    }

    /**
     * Sets the type of the message.
     *
     * @param type the type of the message.
     * @throws IllegalArgumentException if null is passed in as the type
     */
    public void setType(Type type) {
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null.");
        }
        this.type = type;
    }


    public String toXML() {
        StringBuilder buf = new StringBuilder();
        buf.append("<message");
        if (getXmlns() != null) {
            buf.append(" xmlns=\"").append(getXmlns()).append("\"");
        }
        if (getPacketID() != null) {
            buf.append(" id=\"").append(getPacketID()).append("\"");
        }
        if (getTo() != null) {
            buf.append(" to=\"").append(XmppStringUtils.escapeForXML(getTo())).append("\"");
        }
        if (getFrom() != null) {
            buf.append(" from=\"").append(XmppStringUtils.escapeForXML(getFrom())).append("\"");
        }
        if (type != Type.normal) {
            buf.append(" type=\"").append(type).append("\"");
        }
        if (subType != null) {
            buf.append(" subType=\"").append(subType).append("\"");
        }
        buf.append(">");
        if (getBody() != null) {
            buf.append("<body>").append(getBody()).append("</body>");
        }
        if (getSenderNickName() != null) {
            buf.append("<nick>").append(getSenderNickName()).append("</nick>");
        }
        for (PacketExtension extension : getExtensions()) {
            buf.append(extension.toXML());
        }
        if (type == Type.error) {
            XMPPError error = getError();
            if (error != null) {
                buf.append(error.toXML());
            }
        }
        // Add packet extensions, if any are defined.
        buf.append(getExtensionsXML());
        buf.append("</message>");
        return buf.toString();
    }

    public MessageSubType getSubType() {
        return subType;
    }

    public void setSubType(MessageSubType subType) {
        this.subType = subType;
    }

    public String getSenderNickName() {
        return senderNickName;
    }

    public void setSenderNickName(String senderNickName) {
        this.senderNickName = senderNickName;
    }

    public boolean isOffline() {
        return isOffline;
    }

    public void setOffline(boolean isOffline) {
        this.isOffline = isOffline;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Represents the type of a message.
     */
    public enum Type {

        /**
         * (Default) a normal text message used in email like interface.
         */
        normal,

        /**
         * Typically short text message used in line-by-line chat interfaces.
         */
        chat,

        /**
         * Chat message sent to a groupchat server for group chats.
         */
        groupchat,

        /**
         * Text message to be displayed in scrolling marquee displays.
         */
        headline,

        /**
         * indicates a messaging error.
         */
        error;

        public static Type fromString(String name) {
            try {
                return Type.valueOf(name);
            } catch (Exception e) {
                return normal;
            }
        }

    }


}
