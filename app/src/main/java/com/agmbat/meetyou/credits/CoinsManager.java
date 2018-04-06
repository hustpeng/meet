package com.agmbat.meetyou.credits;

import com.agmbat.android.task.AsyncTask;
import com.agmbat.android.task.AsyncTaskUtils;
import com.agmbat.imsdk.asmack.XMPPManager;

import org.jivesoftware.smack.util.XmppStringUtils;

public class CoinsManager {

    public interface OnLoadRecordsListener {
        public void onLoadRecords(CoinsApiResult result);
    }

    /**
     * 加载首页记录
     *
     * @param l
     */
    public void loadFirst(final OnLoadRecordsListener l) {
        AsyncTaskUtils.executeAsyncTask(new AsyncTask<Void, Void, CoinsApiResult>() {
            @Override
            protected CoinsApiResult doInBackground(Void... voids) {
                return request();
            }

            @Override
            protected void onPostExecute(CoinsApiResult result) {
                super.onPostExecute(result);
                if (l != null) {
                    l.onLoadRecords(result);
                }
            }
        });
    }

    /**
     * 加载下一页记录
     *
     * @param l
     */
    public void loadMore(final int index, final OnLoadRecordsListener l) {
        AsyncTaskUtils.executeAsyncTask(new AsyncTask<Void, Void, CoinsApiResult>() {
            @Override
            protected CoinsApiResult doInBackground(Void... voids) {
                return request(index);
            }

            @Override
            protected void onPostExecute(CoinsApiResult result) {
                super.onPostExecute(result);
                if (l != null) {
                    l.onLoadRecords(result);
                }
            }
        });
    }

    private CoinsApiResult request() {
        return request(1);
    }

    private CoinsApiResult request(int pageIndex) {
        String user = XMPPManager.getInstance().getXmppConnection().getUser();
        String phone = XmppStringUtils.parseName(user);
        String token = XMPPManager.getInstance().getTokenManager().getTokenRetry();
        return CoinsApi.getCoins(phone, token, pageIndex);
    }


}
