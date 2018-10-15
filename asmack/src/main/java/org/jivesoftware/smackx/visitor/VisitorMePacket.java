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

package org.jivesoftware.smackx.visitor;

import org.jivesoftware.smack.packet.IQ;

import java.util.ArrayList;

/**
 * Represents XMPP roster packets.
 *
 * @author Matt Tucker
 */
public class VisitorMePacket extends IQ {

    private final ArrayList<VisitorMeObject> visitorMeItems = new ArrayList<VisitorMeObject>();

    /**
     * Adds a roster item to the packet.
     *
     * @param item a roster item.
     */
    public void addVisitorMeItem(VisitorMeObject item) {
        synchronized (visitorMeItems) {
            visitorMeItems.add(item);
        }
    }

    public ArrayList<VisitorMeObject> getVisitorMeItems() {
        synchronized (visitorMeItems) {
            return visitorMeItems;
        }
    }

    public String getChildElementXML() {
        return new StringBuffer()
                .append("<")
                .append(VisitorMeProvider.elementName())
                .append(" xmlns=\"")
                .append(VisitorMeProvider.namespace())
                .append("\"/>")
                .toString();
    }
}
