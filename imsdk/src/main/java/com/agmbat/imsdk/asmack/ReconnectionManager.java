package com.agmbat.imsdk.asmack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.agmbat.android.AppResources;
import com.agmbat.android.utils.NetworkUtil;
import com.agmbat.imsdk.util.AppConfigUtils;
import com.agmbat.log.Debug;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.StreamError;
import org.jivesoftware.smackx.reconnection.ReconnectionListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class ReconnectionManager {

    private Connection connection;
    private Thread reconnectionThread;
    private Thread autoLoginThread;
    private final List<ReconnectionListener> listeners;
    boolean isReconnecting = false;
    boolean isAutoLogin = false;
    Object networkWaittingLock = new Object();

    public ReconnectionManager(Connection connection) {
        this.connection = connection;
        Connection.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(Connection connection) {
                connection.addConnectionListener(connectionListener);
            }
        });

        listeners = new CopyOnWriteArrayList<ReconnectionListener>();

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        AppResources.getAppContext().registerReceiver(mReceiver, filter);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            synchronized (networkWaittingLock) {
                networkWaittingLock.notifyAll();
            }
        }
    };

    @Override
    protected void finalize() throws Throwable {
        AppResources.getAppContext().unregisterReceiver(mReceiver);
        super.finalize();
    }

    public void addListener(ReconnectionListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);

            if (isReconnecting) {
                listener.notifyReconnectionStart();
            }

            if (isAutoLogin) {
                listener.notifyAutoLoginStart();
            }
        }
    }

    public void removeListener(ReconnectionListener listener) {
        listeners.remove(listener);
    }

    private void notifyReconnectionStart() {
        isReconnecting = true;
        for (ReconnectionListener listener : listeners) {
            listener.notifyReconnectionStart();
        }
    }

    private void notifyReconnectionFailed() {
        isReconnecting = false;
        for (ReconnectionListener listener : listeners) {
            listener.notifyReconnectionFailed();
        }
    }

    private void notifyReconnectionSuccess() {
        isReconnecting = false;
        for (ReconnectionListener listener : listeners) {
            listener.notifyReconnectionSuccess();
        }
    }

    private void notifyAutoLoginStart() {
        isAutoLogin = true;
        for (ReconnectionListener listener : listeners) {
            listener.notifyAutoLoginStart();
        }
    }

    private void notifyAutoLoginFailed() {
        isAutoLogin = false;
        for (ReconnectionListener listener : listeners) {
            listener.notifyAutoLoginFailed();
        }
    }

    private void notifyAutoLoginSuccess() {
        isAutoLogin = false;
        for (ReconnectionListener listener : listeners) {
            listener.notifyAutoLoginSuccess();
        }
    }

    private ConnectionListener connectionListener = new ConnectionListener() {
        @Override
        public void loginSuccessful() {
        }

        @Override
        public void connectionClosedOnError(Exception e) {
            if (e instanceof XMPPException) {
                XMPPException xmppEx = (XMPPException) e;
                StreamError error = xmppEx.getStreamError();

                if (error != null) {
                    String reason = error.getCode();
                    if ("conflict".equals(reason)) {
                        return;
                    }
                }
            }

            if (!isAutoLogin && !isReconnecting && isReconnectionAllowed()) {
                reconnect();
            }
        }

        @Override
        public void connectionClosed() {
        }
    };

    /**
     * Returns true if the reconnection mechanism is enabled.
     *
     * @return true if automatic reconnections are allowed.
     */
    private boolean isReconnectionAllowed() {
        return connection.isReconnectionAllowed();
    }

    synchronized protected void reconnect() {
        if (reconnectionThread != null && reconnectionThread.isAlive()) {
            return;
        }

        notifyReconnectionStart();

        reconnectionThread = new Thread() {
            int retryNum = 3;

            public void run() {
                while (ReconnectionManager.this.isReconnectionAllowed() && retryNum > 0) {
                    while (!NetworkUtil.isNetworkAvailable()) {
                        retryNum = 3;
                        synchronized (networkWaittingLock) {
                            try {
                                networkWaittingLock.wait(60 * 1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    try {
                        connection.connect();
                        connection.login(connection.getConfiguration().getUsername(), connection
                                .getConfiguration().getPassword());

                        notifyReconnectionSuccess();
                        return;
                    } catch (Exception e) {
                        retryNum--;
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }

                notifyReconnectionFailed();
            }
        };
        reconnectionThread.setName("Smack Reconnection Manager");
        reconnectionThread.setDaemon(true);
        reconnectionThread.start();
    }

    public synchronized void autoLogin() {
        Debug.printStackTrace();
        if (isAutoLogin || isReconnecting) {
            return;
        }

        final String userName = AppConfigUtils.getUserName(AppResources.getAppContext());
        final String password = AppConfigUtils.getPassword(AppResources.getAppContext());
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
            return;
        }

        new AsyncTask<Integer, String, Boolean>() {
            @Override
            protected void onPreExecute() {
                notifyAutoLoginStart();
                super.onPreExecute();
            }

            @Override
            protected Boolean doInBackground(Integer... params) {
                try {
                    XMPPManager.getInstance().signIn(userName, password);
                } catch (XMPPException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    notifyAutoLoginSuccess();
                } else {
                    notifyAutoLoginFailed();
                }
                super.onPostExecute(result);
            }
        }.execute();
    }
}
