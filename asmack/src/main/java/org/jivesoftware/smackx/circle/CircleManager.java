package org.jivesoftware.smackx.circle;

import android.text.TextUtils;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smackx.token.TokenListener;
import org.jivesoftware.smackx.xepmodule.Xepmodule;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CircleManager extends Xepmodule {

    private final List<TokenListener> listeners;
    public Connection xmppConnection;
    private String circleServer = null;

    private boolean requestTokenAfterSetServer = false;

    private ConnectionListener myConnectionListener = new ConnectionListener() {
        @Override
        public void loginSuccessful() {
            // 每次登陆成功后自动刷一次token
//            new Thread() {
//                @Override
//                public void run() {
//                    getTokenRetry();
//                }
//            }.start();
        }

        @Override
        public void connectionClosedOnError(Exception e) {
            abortAllQuery();
        }

        @Override
        public void connectionClosed() {
            abortAllQuery();
        }
    };

    public CircleManager(final Connection connection) {
        this.xmppConnection = connection;
        Connection.addConnectionCreationListener(new ConnectionCreationListener() {

            @Override
            public void connectionCreated(Connection connection) {
                connection.addConnectionListener(myConnectionListener);
            }
        });
        listeners = new CopyOnWriteArrayList<TokenListener>();
    }


    public void setCircleServer(String circleServer) {
        this.circleServer = circleServer;
        if (!TextUtils.isEmpty(circleServer)) {
            if (requestTokenAfterSetServer) {
                requestTokenAfterSetServer = false;
//                getToken();
            }
        }
    }


}
