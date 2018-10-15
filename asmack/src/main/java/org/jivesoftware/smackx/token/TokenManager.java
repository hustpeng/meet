package org.jivesoftware.smackx.token;

import android.text.TextUtils;

import com.agmbat.log.Log;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.xepmodule.XepQueryInfo;
import org.jivesoftware.smackx.xepmodule.Xepmodule;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TokenManager extends Xepmodule {

    private final static int getToken = 0;
    private static final String TAG = "";
    private final List<TokenListener> listeners;
    private String tokenServer = null;
    private boolean requestTokenAfterSetServer = false;
    private TokenObject tokenObject = null;
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
    private Object tokenLock = new Object();

    public TokenManager(final Connection connection) {
        this.xmppConnection = connection;
        Connection.addConnectionCreationListener(new ConnectionCreationListener() {

            @Override
            public void connectionCreated(Connection connection) {
                connection.addConnectionListener(myConnectionListener);
            }
        });
        listeners = new CopyOnWriteArrayList<TokenListener>();
    }

    public void addListener(TokenListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(TokenListener listener) {
        listeners.remove(listener);
    }

    private void notifyGetTokenResult(String token) {
        for (TokenListener listener : listeners) {
            listener.notifyGetTokenResult(token);
        }
    }

    @Override
    public void processQueryWithFailureCode(XepQueryInfo queryInfo, String error) {
        switch (queryInfo.getQueryType()) {
            case getToken:
                notifyGetTokenResult(null);
                break;

            default:
                break;
        }
    }

    private void processQueryResponse(Packet packet, XepQueryInfo queryInfo) {
        switch (queryInfo.getQueryType()) {
            case getToken: {
                if (packet.getError() == null) {
                    TokenObject token = ((TokenPacket) packet).getObject();
                    if (token != null) {
                        tokenObject = token;
                        notifyGetTokenResult(token.getToken());
                        break;
                    }
                }

                notifyGetTokenResult(null);
            }
            break;

            default:
                break;
        }
    }

    public String getToken() {
        if (!xmppConnection.isConnected()) {
            return null;
        }

        if (tokenObject != null) {
            if (!TextUtils.isEmpty(tokenObject.getToken())) {
                long expirdTime = tokenObject.getExpirdTime();
                long currentTime = System.currentTimeMillis();
                if (currentTime < (expirdTime - 30)) {
                    return tokenObject.getToken();
                }
            }
        }

        if (TextUtils.isEmpty(tokenServer)) {
            requestTokenAfterSetServer = true;
            return null;
        }

        if (isQueryExist(getToken, null, null)) {
            return null;
        }

        getTokenPacket packet = new getTokenPacket();

        String packetId = packet.getPacketID();
        PacketFilter idFilter = new PacketIDFilter(packetId);
        GetTokenResultListener packetListener = new GetTokenResultListener();
        xmppConnection.addPacketListener(packetListener, idFilter);

        XepQueryInfo queryInfo = new XepQueryInfo(getToken);
        addQueryInfo(queryInfo, packetId, packetListener);

        xmppConnection.sendPacket(packet);
        return null;
    }

    public void setTokenServer(String tokenServer) {
        this.tokenServer = tokenServer;
        if (!TextUtils.isEmpty(tokenServer)) {
            if (requestTokenAfterSetServer) {
                requestTokenAfterSetServer = false;
                getToken();
            }
        }
    }

    /**
     * 获取token
     *
     * @return
     */
    public String getTokenRetry() {
        int retry = 4;
        String tokenString = null;
        while ((retry--) > 0) {
            tokenString = getToken();
            if (tokenString == null) {
                synchronized (tokenLock) {
                    try {
                        Log.d(TAG, "tokenLock.wait start");
                        tokenLock.wait(10 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "tokenLock.wait end");
                }
            } else {
                return tokenString;
            }
        }
        return tokenString;
    }

    public class getTokenPacket extends IQ {

        public getTokenPacket() {
            setTo(tokenServer);
        }

        @Override
        public String getChildElementXML() {
            return new StringBuffer()
                    .append("<")
                    .append(TokenProvider.elementName())
                    .append(" xmlns=\"")
                    .append(TokenProvider.namespace())
                    .append("\"/>")
                    .toString();
        }
    }

    private class GetTokenResultListener implements PacketListener {
        @Override
        public void processPacket(Packet packet) {
            String packetIdString = packet.getPacketID();
            XepQueryInfo queryInfo = getQueryInfo(packetIdString);
            if (queryInfo != null) {
                removeQueryInfo(queryInfo, packetIdString);
                processQueryResponse(packet, queryInfo);
            }
        }
    }
}
