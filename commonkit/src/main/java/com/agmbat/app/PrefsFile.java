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
package com.agmbat.app;

import java.io.File;
import java.util.Vector;

import com.agmbat.android.AppResources;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

/**
 * 管理app配置文件
 */
public class PrefsFile implements OnSharedPreferenceChangeListener {

    private static final String DEFAULT_PRE_FILE = "prefs";

    private final Vector<OnSharedPreferenceChangeListener> mListeners = new Vector<OnSharedPreferenceChangeListener>();

    public PrefsFile() {
        getPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        synchronized (this) {
            mListeners.add(listener);
        }
    }

    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        synchronized (this) {
            mListeners.remove(listener);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        for (OnSharedPreferenceChangeListener l : mListeners) {
            l.onSharedPreferenceChanged(sharedPreferences, key);
        }
    }

    public SharedPreferences getPreferences() {
        return getPreferences(getPreferencesFileName(), Context.MODE_PRIVATE);
    }

    protected SharedPreferences getPreferences(String name, int mode) {
        Context context = AppResources.getAppContext();
        return context.getSharedPreferences(name, mode);
    }

    protected File getPreferencesDir() {
        return null;
    }

    protected String getPreferencesFileName() {
        return DEFAULT_PRE_FILE;
    }

    public String getStringValue(String key, String defValue) {
        return getPreferences().getString(key, defValue);
    }

    public boolean setStringValue(String key, String value) {
        Editor editor = getPreferences().edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public int getIntValue(String key, int defValue) {
        return getPreferences().getInt(key, defValue);
    }

    public boolean setIntValue(String key, int value) {
        Editor editor = getPreferences().edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    public long getLongValue(String key, long defValue) {
        return getPreferences().getLong(key, defValue);
    }

    public boolean setLongValue(String key, long value) {
        Editor editor = getPreferences().edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    public boolean getBooleanValue(String key, boolean defValue) {
        return getPreferences().getBoolean(key, defValue);
    }

    public boolean setBooleanValue(String key, boolean value) {
        Editor editor = getPreferences().edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public float getFloatValue(String key, float defValue) {
        return getPreferences().getFloat(key, defValue);
    }

    public boolean setFloatValue(String key, float value) {
        Editor editor = getPreferences().edit();
        editor.putFloat(key, value);
        return editor.commit();
    }

    public void clear() {
        getPreferences().edit().clear().commit();
    }

    public void remove(String key) {
        getPreferences().edit().remove(key).commit();
    }
}
