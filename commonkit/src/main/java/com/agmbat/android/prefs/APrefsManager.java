/*
 * Copyright (C) 2015 mayimchen <mayimchen@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.agmbat.android.prefs;

import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 支持将prefs文件保存到外部存储器中
 */
public class APrefsManager {

    private final static String TAG = APrefsManager.class.getSimpleName();
    private static final HashMap<File, SharedPreferencesImpl> sSharedPrefs = new HashMap<File, SharedPreferencesImpl>();
    private final Object mSync = new Object();
    private File mPreferencesDir;

    public File getSharedPrefsFile(String name) {
        return makeFilename(getPreferencesDir(), name + ".xml");
    }

    public SharedPreferences getSharedPreferences(String name) {
        SharedPreferencesImpl sp;
        File f = getSharedPrefsFile(name);
        synchronized (sSharedPrefs) {
            sp = sSharedPrefs.get(f);
            if (sp != null && !sp.hasFileChanged()) {
                Log.v(TAG, "Returning existing prefs " + name + ": " + sp);
                return sp;
            }
        }

        FileInputStream str = null;
        File backup = SharedPreferencesImpl.makeBackupFile(f);
        if (backup.exists()) {
            f.delete();
            backup.renameTo(f);
        }

        // Debugging
        if (f.exists() && !f.canRead()) {
            Log.w(TAG, "Attempt to read preferences file " + f + " without permission");
        }

        Map map = null;
        if (f.exists() && f.canRead()) {
            try {
                str = new FileInputStream(f);
                map = XmlUtils.readMapXml(str);
                str.close();
            } catch (Exception e) {
                Log.w(TAG, "getSharedPreferences", e);
            }
        }

        synchronized (sSharedPrefs) {
            if (sp != null) {
                // Log.i(TAG, "Updating existing prefs " + name + " " + sp +
                // ": " + map);
                sp.replace(map);
            } else {
                sp = sSharedPrefs.get(f);
                if (sp == null) {
                    sp = new SharedPreferencesImpl(f, map);
                    sSharedPrefs.put(f, sp);
                }
            }
            return sp;
        }
    }

    private File getPreferencesDir() {
        synchronized (mSync) {
            if (mPreferencesDir == null) {
                mPreferencesDir = new File(getDataDirFile(), "shared_prefs");
            }
            return mPreferencesDir;
        }
    }

    public void setPreferencesDir(File dir) {
        mPreferencesDir = dir;
    }

    private File makeFilename(File base, String name) {
        if (name.indexOf(File.separatorChar) < 0) {
            return new File(base, name);
        }
        throw new IllegalArgumentException("File " + name + " contains a path separator");
    }

    // TODO这里修改文件存放目录
    private File getDataDirFile() {
        // 修改了 ContextImpl代码中的实现
        File dataDirFile = new File(Environment.getExternalStorageDirectory(), "data");
        ensureDir(dataDirFile);
        return dataDirFile;
    }

    private void ensureDir(File dir) {
        if (dir.exists()) {
            if (dir.isDirectory()) {
                return;
            } else {
                dir.delete();
            }
        }
        dir.mkdirs();
    }

}
