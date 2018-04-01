/**
 * $RCSfile$
 * $Revision$
 * $Date$
 *
 * Copyright 2003-2007 Jive Software.
 *
 * All rights reserved. Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jivesoftware.smackx.message;

import org.jivesoftware.smack.packet.Message.SubType;
import org.jivesoftware.smack.packet.PacketExtension;

public class MessageHtmlExtension implements PacketExtension{
    private SubType messageType;
    private double latDouble;
    private double lonDouble;
    private String imageSrcString;
    private String imageThumbString;

    public MessageHtmlExtension(SubType type, double lat, double lon)
    {
        messageType = type;
        latDouble = lat;
        lonDouble = lon;
    }

    public MessageHtmlExtension(SubType type, String src, String thumb)
    {
        messageType = type;
        imageSrcString = src;
        imageThumbString = thumb;
    }

    @Override
    public String getElementName() {
        return MessageHtmlProvider.elementName();
    }

    @Override
    public String getNamespace() {
        return MessageHtmlProvider.namespace();
    }

    @Override
    public String toXML() {
        if (messageType == SubType.geoloc) {
            StringBuilder buf = new StringBuilder();
            buf.append("<");
            buf.append(MessageHtmlProvider.elementName());
            buf.append(" xmlns=\"");
            buf.append(MessageHtmlProvider.namespace());
            buf.append("\">");
            buf.append("<body><geoloc lat=\"");
            buf.append(latDouble).append("\" lon=\"").append(lonDouble);
            buf.append("\"/></body></");
            buf.append(MessageHtmlProvider.elementName());
            buf.append(">");
            return buf.toString();
        } else if (messageType == SubType.image) {
            StringBuilder buf = new StringBuilder();
            buf.append("<");
            buf.append(MessageHtmlProvider.elementName());
            buf.append(" xmlns=\"");
            buf.append(MessageHtmlProvider.namespace());
            buf.append("\">");
            buf.append("<body><img src=\"");
            buf.append(imageSrcString).append("\" thumb=\"").append(imageThumbString);
            buf.append("\"/></body></");
            buf.append(MessageHtmlProvider.elementName());
            buf.append(">");
            return buf.toString();
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return toXML();
    }
}