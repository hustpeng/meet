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

package org.jivesoftware.smackx.favorites;

import org.jivesoftware.smack.packet.IQ;

import java.util.ArrayList;

/**
 * Represents XMPP roster packets.
 *
 * @author Matt Tucker
 */
public class FavoritesPacket extends IQ {

    private final ArrayList<FavoritesObject> favoritesItems = new ArrayList<FavoritesObject>();

    /**
     * Adds a roster item to the packet.
     *
     * @param item a roster item.
     */
    public void addFavoritesItem(FavoritesObject item) {
        synchronized (favoritesItems) {
            favoritesItems.add(item);
        }
    }

    public ArrayList<FavoritesObject> getFavoritesItems() {
        synchronized (favoritesItems) {
            return favoritesItems;
        }
    }

    public String getChildElementXML() {
        return new StringBuffer()
                    .append("<")
                    .append(FavoritesProvider.elementName())
                    .append(" xmlns=\"")
                    .append(FavoritesProvider.namespace())
                    .append("\"/>")
                    .toString();
    }
}
