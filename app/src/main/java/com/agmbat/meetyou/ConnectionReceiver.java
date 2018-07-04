package com.agmbat.meetyou;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.log.Log;

public class ConnectionReceiver extends BroadcastReceiver {

    private static final String TAG = ConnectionReceiver.class.getSimpleName();

    private static ConnectionReceiver sReceiver = new ConnectionReceiver();

    public static void register(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(sReceiver, filter);
    }

    public static void unregister(Context context) {
        context.unregisterReceiver(sReceiver);
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        String action = intent.getAction();
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            // 处理手机的网络断开广播
            Log.i(TAG, "Connectivity change...");
            boolean connected = !intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            if (connected) {
                XMPPManager.getInstance().getReconnectionManager().autoLogin();
            }
        }
    }
}