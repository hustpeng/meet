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

    /**
     * 默认显示的loading框
     */
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
