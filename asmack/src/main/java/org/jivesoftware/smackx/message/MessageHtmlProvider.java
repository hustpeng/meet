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
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;

public class MessageHtmlProvider implements PacketExtensionProvider {
    public MessageHtmlProvider() {
    }

    public static String elementName()
    {
        return "html";
    }

    public static String namespace()
    {
        return "http://jabber.org/protocol/xhtml-im";
    }

    public PacketExtension parseExtension(XmlPullParser parser)
        throws Exception {

        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG) {
                if (parser.getName().equals("img")) {
                    String src = parser.getAttributeValue("", "src");
                    String thumb = parser.getAttributeValue("", "thumb");

                    return new MessageHtmlExtension(SubType.image, src, thumb);
                } else if (parser.getName().equals("geoloc")) {
                    String latString = parser.getAttributeValue("", "lat");
                    String lonString = parser.getAttributeValue("", "lon");
                    double lat = 0;
                    double lon = 0;

                    try {
                        lat = Double.valueOf(latString);
                        lon = Double.valueOf(lonString);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                    return new MessageHtmlExtension(SubType.geoloc, lat, lon);
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if (parser.getName().equals("html")) {
                    done = true;
                }
            }
        }

        return null;
    }

}
