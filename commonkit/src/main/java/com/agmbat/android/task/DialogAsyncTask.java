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
package com.agmbat.android.task;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * 可以显示对话框的异步任务
 *
 * @param <Params>
 * @param <Progress>
 * @param <Result>
 */
public abstract class DialogAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    private Context mContext;
    private ProgressDialog mDialog;

    public DialogAsyncTask(Context context) {
        mContext = context;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog = new ProgressDialog(mContext);
        mDialog.setCancelable(false);
        onPrepareDialog(mDialog);
        mDialog.show();
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
        try {
            mDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected Context getContext() {
        return mContext;
    }

    protected void onPrepareDialog(ProgressDialog dialog) {
    }

}
