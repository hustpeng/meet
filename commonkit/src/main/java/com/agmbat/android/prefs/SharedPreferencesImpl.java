package com.agmbat.android.prefs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;

import android.content.SharedPreferences;
import android.util.Log;

public final class SharedPreferencesImpl implements SharedPreferences {

    private static final String TAG = SharedPreferencesImpl.class.getSimpleName();

    private final File mFile;
    private final File mBackupFile;
    private Map mMap;
    private long mTimestamp;

    private static final Object mContent = new Object();
    private WeakHashMap<OnSharedPreferenceChangeListener, Object> mListeners;

    SharedPreferencesImpl(File file, Map initialContents) {
        mFile = file;
        mBackupFile = makeBackupFile(file);
        mMap = initialContents != null ? initialContents : new HashMap();
        if (file.exists()) {
            mTimestamp = file.lastModified();
        }
        mListeners = new WeakHashMap<OnSharedPreferenceChangeListener, Object>();
    }

    public boolean hasFileChanged() {
        synchronized (this) {
            if (!mFile.exists()) {
                return true;
            }
            return mTimestamp != mFile.lastModified();
        }
    }

    public void replace(Map newContents) {
        if (newContents != null) {
            synchronized (this) {
                mMap = newContents;
            }
        }
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        synchronized (this) {
            mListeners.put(listener, mContent);
        }
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        synchronized (this) {
            mListeners.remove(listener);
        }
    }

    @Override
    public Map<String, ?> getAll() {
        synchronized (this) {
            // noinspection unchecked
            return new HashMap(mMap);
        }
    }

    @Override
    public String getString(String key, String defValue) {
        synchronized (this) {
            String v = (String) mMap.get(key);
            return v != null ? v : defValue;
        }
    }

    @Override
    public int getInt(String key, int defValue) {
        synchronized (this) {
            Integer v = (Integer) mMap.get(key);
            return v != null ? v : defValue;
        }
    }

    @Override
    public long getLong(String key, long defValue) {
        synchronized (this) {
            Long v = (Long) mMap.get(key);
            return v != null ? v : defValue;
        }
    }

    @Override
    public float getFloat(String key, float defValue) {
        synchronized (this) {
            Float v = (Float) mMap.get(key);
            return v != null ? v : defValue;
        }
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        synchronized (this) {
            Boolean v = (Boolean) mMap.get(key);
            return v != null ? v : defValue;
        }
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        return null;
    }

    @Override
    public boolean contains(String key) {
        synchronized (this) {
            return mMap.containsKey(key);
        }
    }

    public final class EditorImpl implements Editor {
        private final Map<String, Object> mModified = new HashMap<String, Object>();
        private boolean mClear = false;

        @Override
        public Editor putString(String key, String value) {
            synchronized (this) {
                mModified.put(key, value);
                return this;
            }
        }

        @Override
        public Editor putInt(String key, int value) {
            synchronized (this) {
                mModified.put(key, value);
                return this;
            }
        }

        @Override
        public Editor putLong(String key, long value) {
            synchronized (this) {
                mModified.put(key, value);
                return this;
            }
        }

        @Override
        public Editor putFloat(String key, float value) {
            synchronized (this) {
                mModified.put(key, value);
                return this;
            }
        }

        @Override
        public Editor putBoolean(String key, boolean value) {
            synchronized (this) {
                mModified.put(key, value);
                return this;
            }
        }

        @Override
        public Editor remove(String key) {
            synchronized (this) {
                mModified.put(key, this);
                return this;
            }
        }

        @Override
        public Editor clear() {
            synchronized (this) {
                mClear = true;
                return this;
            }
        }

        @Override
        public boolean commit() {
            boolean returnValue;

            boolean hasListeners;
            List<String> keysModified = null;
            Set<OnSharedPreferenceChangeListener> listeners = null;

            synchronized (SharedPreferencesImpl.this) {
                hasListeners = mListeners.size() > 0;
                if (hasListeners) {
                    keysModified = new ArrayList<String>();
                    listeners = new HashSet<OnSharedPreferenceChangeListener>(mListeners.keySet());
                }
                synchronized (this) {
                    if (mClear) {
                        mMap.clear();
                        mClear = false;
                    }
                    for (Entry<String, Object> e : mModified.entrySet()) {
                        String k = e.getKey();
                        Object v = e.getValue();
                        if (v == this) {
                            mMap.remove(k);
                        } else {
                            mMap.put(k, v);
                        }
                        if (hasListeners) {
                            keysModified.add(k);
                        }
                    }
                    mModified.clear();
                }
                returnValue = writeFileLocked();
            }

            if (hasListeners) {
                for (int i = keysModified.size() - 1; i >= 0; i--) {
                    final String key = keysModified.get(i);
                    for (OnSharedPreferenceChangeListener listener : listeners) {
                        if (listener != null) {
                            listener.onSharedPreferenceChanged(SharedPreferencesImpl.this, key);
                        }
                    }
                }
            }
            return returnValue;
        }

        @Override
        public Editor putStringSet(String key, Set<String> values) {
            return null;
        }

        @Override
        public void apply() {
        }

    }

    public Editor edit() {
        return new EditorImpl();
    }

    private FileOutputStream createFileOutputStream(File file) {
        FileOutputStream str = null;
        try {
            str = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            File parent = file.getParentFile();
            if (!parent.mkdir()) {
                Log.e(TAG, "Couldn't create directory for SharedPreferences file " + file);
                return null;
            }
            try {
                str = new FileOutputStream(file);
            } catch (FileNotFoundException e2) {
                Log.e(TAG, "Couldn't create SharedPreferences file " + file, e2);
            }
        }
        return str;
    }

    private boolean writeFileLocked() {
        // Rename the current file so it may be used as a backup during the next
        // read
        if (mFile.exists()) {
            if (!mBackupFile.exists()) {
                if (!mFile.renameTo(mBackupFile)) {
                    Log.e(TAG, "Couldn't rename file " + mFile + " to backup file " + mBackupFile);
                    return false;
                }
            } else {
                mFile.delete();
            }
        }

        // Attempt to write the file, delete the backup and return true as
        // atomically as
        // possible. If any exception occurs, delete the new file; next time we
        // will restore
        // from the backup.
        try {
            FileOutputStream str = createFileOutputStream(mFile);
            if (str == null) {
                return false;
            }
            XmlUtils.writeMapXml(mMap, str);
            str.close();
            if (mFile.exists()) {
                mTimestamp = mFile.lastModified();
            }

            // Writing was successful, delete the backup file if there is one.
            mBackupFile.delete();
            return true;
        } catch (Exception e) {
            Log.w(TAG, "writeFileLocked: Got exception:", e);
        }
        // Clean up an unsuccessfully written file
        if (mFile.exists()) {
            if (!mFile.delete()) {
                Log.e(TAG, "Couldn't clean up partially-written file " + mFile);
            }
        }
        return false;
    }

    static File makeBackupFile(File prefsFile) {
        return new File(prefsFile.getPath() + ".bak");
    }

}
