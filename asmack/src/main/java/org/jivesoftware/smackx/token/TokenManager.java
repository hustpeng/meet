
package org.jivesoftware.smackx.token;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.xepmodule.xepmodule;

import android.text.TextUtils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TokenManager extends xepmodule{
    private final static int getToken = 0;

    private String tokenServer = null;

    private boolean requestTokenAfterSetServer = false;

    private TokenObject tokenObject = null;

    private final List<TokenListener> listeners;

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

    private ConnectionListener myConnectionListener = new ConnectionListener() {
        @Override
        public void loginSuccessful() {
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

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public void addListener(TokenListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(TokenListener listener) {
        listeners.remove(listener);
    }

    private void notifyGetTokenResult(String token)
    {
        for (TokenListener listener : listeners) {
            listener.notifyGetTokenResult(token);
        }
    }

    @Override
    public void processQueryWithFailureCode(xepQueryInfo queryInfo, String error)
    {
        switch (queryInfo.getQueryType()) {
            case getToken:
                notifyGetTokenResult(null);
                break;

            default:
                break;
        }
    }

    private void processQueryResponse(Packet packet, xepQueryInfo queryInfo)
    {
        switch (queryInfo.getQueryType()) {
            case getToken:
            {
                if (packet.getError() == null) {
                    TokenObject token = ((TokenPacket)packet).getObject();
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

    public class getTokenPacket extends IQ {

        public getTokenPacket() {
            setTo(tokenServer);
        }

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

        xepQueryInfo queryInfo = new xepQueryInfo(getToken);
        addQueryInfo(queryInfo, packetId, packetListener);

        xmppConnection.sendPacket(packet);
        return null;
    }

    public String getTokenServer() {
        return tokenServer;
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

    private class GetTokenResultListener implements PacketListener{
        public void processPacket(Packet packet) {
            String packetIdString = packet.getPacketID();
            xepQueryInfo queryInfo = getQueryInfo(packetIdString);
            if (queryInfo != null) {
                removeQueryInfo(queryInfo, packetIdString);
                processQueryResponse(packet, queryInfo);
            }
        }
    }
}
