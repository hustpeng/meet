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

import java.util.ArrayList;

public class VisitorRecordStorage {
    private VisitorRecordDBStoreProvider dbStoreProvider;

    public VisitorRecordStorage() {
        dbStoreProvider = new VisitorRecordDBStoreProvider();
    }

    public void insertOrUpdateVisitorRecord(VisitorRecordObject object) {
        // modify by jack----Note:"update" is not work if the selection is not
        // in db.
        if (object != null) {
            dbStoreProvider.delete(VisitorRecordDBStoreProvider.Columns.VISITOR_WHO_JID + "=? And "
                    + VisitorRecordDBStoreProvider.Columns.VISITOR_ENTRANCE + "=?", new String[]{
                    object.getVisitorWhoJid(), object.getEntrance()
            });
            dbStoreProvider.insert(object);
            // dbStoreProvider.update(object,
            // VisitorRecordDBStoreProvider.Columns.VISITOR_WHO_JID + "=? And "
            // + VisitorRecordDBStoreProvider.Columns.VISITOR_ENTRANCE + "=?",
            // new String[]{object.getVisitorWhoJid(), object.getEntrance()});
        }
    }

    public ArrayList<VisitorRecordObject> getAllVisitorRecord() {
        ArrayList<VisitorRecordObject> arrayList = dbStoreProvider.query(null, null, null, null);
        return arrayList;
    }

    public void deleteAllVisitorRecord() {
        dbStoreProvider.delete(null, null);
    }
}
