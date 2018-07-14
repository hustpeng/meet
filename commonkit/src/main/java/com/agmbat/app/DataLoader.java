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

import java.util.List;
import java.util.Vector;

import android.os.AsyncTask;

public abstract class DataLoader<T> {

    /**
     * load data
     * 
     * @param <T>
     */
    public interface OnLoadDataListener<T> {

        public void onLoadBegin();

        public void onLoadCompleted(List<T> result);

        public void onLoadCancelled();

    }

    private static enum LoadStatus {
        NOT_START, LOADING, FINISH,
    }

    private List<T> mData = null;
    private Vector<OnLoadDataListener<T>> mOnLoadDataListeners;

    private Object mSyncObject = new Object();
    private LoadStatus mLoadStatus = LoadStatus.NOT_START;

    protected DataLoader() {
        mOnLoadDataListeners = new Vector<OnLoadDataListener<T>>();
    }

    public List<T> getData() {
        return mData;
    }

    private class LoadDataTask extends AsyncTask<Void, Void, List<T>> {

        public LoadDataTask() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            synchronized (mSyncObject) {
                for (OnLoadDataListener<T> l : mOnLoadDataListeners) {
                    l.onLoadBegin();
                }
            }
        }

        @Override
        protected List<T> doInBackground(Void... params) {
            return loadDataSync();
        }

        @Override
        protected void onPostExecute(List<T> result) {
            super.onPostExecute(result);
            synchronized (mSyncObject) {
                mData = result;
                mLoadStatus = LoadStatus.FINISH;
                for (OnLoadDataListener<T> l : mOnLoadDataListeners) {
                    l.onLoadCompleted(result);
                }
                mOnLoadDataListeners.clear();
            }
        }

        @Override
        protected void onCancelled() {
        }
    }

    public abstract List<T> loadDataSync();

    public void loadData() {
        loadData(null);
    }

    public void loadData(OnLoadDataListener<T> l) {
        synchronized (mSyncObject) {
            if (mLoadStatus == LoadStatus.NOT_START) {
                mLoadStatus = LoadStatus.LOADING;
                if (l != null) {
                    mOnLoadDataListeners.add(l);
                }
                new LoadDataTask().execute();
            } else if (mLoadStatus == LoadStatus.LOADING) {
                if (l != null) {
                    l.onLoadBegin();
                    mOnLoadDataListeners.add(l);
                }
            } else {
                if (l != null) {
                    l.onLoadCompleted(mData);
                }
            }
        }
    }

    public void unregisterLoadListener(OnLoadDataListener<T> l) {
        synchronized (mSyncObject) {
            if (mOnLoadDataListeners.contains(l)) {
                mOnLoadDataListeners.remove(l);
            }
        }
    }
}
