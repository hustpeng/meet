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

import com.agmbat.android.AppResources;

import java.util.ArrayList;

public class VisitorMeReadFlagStorage {
    private VisitorMeReadFlagDBStoreProvider dbStoreProvider;

    public VisitorMeReadFlagStorage() {
        dbStoreProvider = new VisitorMeReadFlagDBStoreProvider(AppResources.getAppContext());
    }

    public void insertReadFlag(VisitorMeReadFlagObject obj) {
        // VisitorMeReadFlagObject object = new VisitorMeReadFlagObject();
        // object.setVisitorJid(jidString);
        // object.setCreate_date(System.currentTimeMillis());

        dbStoreProvider.insert(obj);
    }

    public int getNewVisitorCount() {
        // ArrayList<VisitorMeReadFlagObject> arrayList =
        // dbStoreProvider.query(new String[] {
        // VisitorMeReadFlagDBStoreProvider.Columns.VISITORME_READ_VISITOR_JID
        // }, null, null, null);
        ArrayList<VisitorMeReadFlagObject> arrayList = dbStoreProvider
                .query(null, null, null, null);
        return arrayList.size();
    }

    public void deleteReadFlag(String jidString, String entrance) {
        dbStoreProvider.delete(VisitorMeReadFlagDBStoreProvider.Columns.VISITORME_READ_VISITOR_JID
                + "=? and + " + VisitorMeReadFlagDBStoreProvider.Columns.VISITORME_READ_ENTRANCE
                + "= ?", new String[]{
                jidString, entrance
        });
    }

    public boolean isReadFlagExsit(String jidString, String entrance) {
        ArrayList<VisitorMeReadFlagObject> arrayList = dbStoreProvider.query(null,
                VisitorMeReadFlagDBStoreProvider.Columns.VISITORME_READ_VISITOR_JID + "= ? and + "
                        + VisitorMeReadFlagDBStoreProvider.Columns.VISITORME_READ_ENTRANCE + "= ?",
                new String[]{
                        jidString, entrance
                }, null);

        if (arrayList.size() > 0) {
            return true;
        }

        return false;
    }
}
