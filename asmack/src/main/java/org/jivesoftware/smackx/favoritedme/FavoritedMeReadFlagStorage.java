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

package org.jivesoftware.smackx.favoritedme;

import com.agmbat.android.AppResources;

import java.util.ArrayList;

public class FavoritedMeReadFlagStorage {
    private FavoritedMeDBStoreProvider dbStoreProvider;

    public FavoritedMeReadFlagStorage() {
        dbStoreProvider = new FavoritedMeDBStoreProvider(AppResources.getAppContext());
    }

    public int getNewFavoritedMeCount() {
        ArrayList<FavoritedMeReadFlagObject> arrayList = dbStoreProvider.query(null, null, null,
                null);
        return arrayList.size();
    }

    public void insertOrUpdateReadFlag(String jidString) {
        FavoritedMeReadFlagObject object = new FavoritedMeReadFlagObject();
        object.setJid(jidString);
        object.setCreate_date(System.currentTimeMillis());
        dbStoreProvider.delete(FavoritedMeDBStoreProvider.Columns.FAVORITEDME_READ_JID + "=?",
                new String[]{
                        jidString
                });
        dbStoreProvider.insert(object);
        // dbStoreProvider.update(object,
        // FavoritedMeDBStoreProvider.Columns.FAVORITEDME_READ_JID + "=?", new
        // String[]{jidString});
    }

    public void deleteReadFlag(String jidString) {
        dbStoreProvider.delete(FavoritedMeDBStoreProvider.Columns.FAVORITEDME_READ_JID + "=?",
                new String[]{
                        jidString
                });
    }

    public boolean isReadFlagExsit(String jidString) {
        ArrayList<FavoritedMeReadFlagObject> arrayList = dbStoreProvider.query(null,
                FavoritedMeDBStoreProvider.Columns.FAVORITEDME_READ_JID + "=?", new String[]{
                        jidString
                }, null);

        if (arrayList.size() > 0) {
            return true;
        }

        return false;
    }
}
