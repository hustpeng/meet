
package org.jivesoftware.smackx.paid;

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

public class PaidManager extends xepmodule{
    private String paidServer = null;

    private boolean requestAccountAfterSetServer = false;

    private PaidAccountObject myAccountInfo;

    private PaidPageInfoObject myPaidPageInfo;

    private final static int fetchAccountInfo = 0;
    private final static int fetchPaidPageInfo = 1;
    private final static int sendPaymentVerify = 2;

    private final List<PaidListener> listeners;

    public PaidManager(final Connection connection) {
        this.xmppConnection = connection;
        Connection.addConnectionCreationListener(new ConnectionCreationListener() {

            @Override
            public void connectionCreated(Connection connection) {
                connection.addConnectionListener(myConnectionListener);
            }
        });
        listeners = new CopyOnWriteArrayList<PaidListener>();
    }

    private ConnectionListener myConnectionListener = new ConnectionListener() {
        @Override
        public void loginSuccessful() {
            fetchAccountInfo();
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

    public void addListener(PaidListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(PaidListener listener) {
        listeners.remove(listener);
    }

    private void notifyFetchAccountResult(PaidAccountObject object) {
        for (PaidListener listener : listeners) {
            listener.notifyFetchAccountResult(object);
        }
    }

    private void notifyFetchPageInfoResult(PaidPageInfoObject object) {
        for (PaidListener listener : listeners) {
            listener.notifyFetchPageInfoResult(object);
        }
    }

    public PaidAccountObject getAccountInfo()
    {
        return myAccountInfo;
    }

    public PaidPageInfoObject getPaidPageInfo()
    {
        return myPaidPageInfo;
    }

    public boolean isSubscriptionVaild()
    {
        if (myAccountInfo != null)
        {
            return myAccountInfo.isSubscriptionVaild();
        }

        return false;
    }

    @Override
    public void processQueryWithFailureCode(xepQueryInfo queryInfo, String error)
    {
        switch (queryInfo.getQueryType()) {
            case fetchAccountInfo:
                notifyFetchAccountResult(myAccountInfo);
                break;

            case fetchPaidPageInfo:
                notifyFetchPageInfoResult(myPaidPageInfo);
                break;

            case sendPaymentVerify:
                break;

            default:
                break;
        }
    }

    private void processQueryResponse(Packet packet, xepQueryInfo queryInfo)
    {
        switch (queryInfo.getQueryType()) {
            case fetchAccountInfo:
                myAccountInfo = ((PaidAccountPacket)packet).getObject();
                notifyFetchAccountResult(myAccountInfo);
                break;

            case fetchPaidPageInfo:
                myPaidPageInfo = ((PaidPageInfoPacket)packet).getObject();
                notifyFetchPageInfoResult(myPaidPageInfo);
                break;

            case sendPaymentVerify:
                break;

            default:
                break;
        }
    }

    public class FetchAccountPacket extends IQ {

        public FetchAccountPacket() {
            setTo(paidServer);
        }

        public String getChildElementXML() {
            return new StringBuffer()
                        .append("<")
                        .append(PaidAccountProvider.elementName())
                        .append(" xmlns=\"")
                        .append(PaidAccountProvider.namespace())
                        .append("\"/>")
                        .toString();
        }
    }

    public void fetchAccountInfo()
    {
        if (TextUtils.isEmpty(paidServer)) {
            requestAccountAfterSetServer = true;
            return;
        }

        if (isQueryExist(fetchAccountInfo, null, null)) {
            return;
        }

        FetchAccountPacket packet = new FetchAccountPacket();

        String packetId = packet.getPacketID();
        PacketFilter idFilter = new PacketIDFilter(packetId);
        paidResultListener packetListener = new paidResultListener();
        xmppConnection.addPacketListener(packetListener, idFilter);

        xepQueryInfo queryInfo = new xepQueryInfo(fetchAccountInfo);
        addQueryInfo(queryInfo, packetId, packetListener);

        xmppConnection.sendPacket(packet);
        return;
    }

    public class FetchPageInfoPacket extends IQ {

        public FetchPageInfoPacket() {
            setTo(paidServer);
        }

        public String getChildElementXML() {
            return new StringBuffer()
                        .append("<")
                        .append(PaidPageInfoProvider.elementName())
                        .append(" xmlns=\"")
                        .append(PaidPageInfoProvider.namespace())
                        .append("\"/>")
                        .toString();
        }
    }

    public void fetchPageInfo()
    {
        if (TextUtils.isEmpty(paidServer)) {
            return;
        }

        if (isQueryExist(fetchPaidPageInfo, null, null)) {
            return;
        }

        FetchPageInfoPacket packet = new FetchPageInfoPacket();

        String packetId = packet.getPacketID();
        PacketFilter idFilter = new PacketIDFilter(packetId);
        paidResultListener packetListener = new paidResultListener();
        xmppConnection.addPacketListener(packetListener, idFilter);

        xepQueryInfo queryInfo = new xepQueryInfo(fetchPaidPageInfo);
        addQueryInfo(queryInfo, packetId, packetListener);

        xmppConnection.sendPacket(packet);
        return;
    }

    public String getPaidServer() {
        return paidServer;
    }

    public void setPaidServer(String paidServer) {
        this.paidServer = paidServer;
        if (!TextUtils.isEmpty(paidServer)) {
            if (requestAccountAfterSetServer) {
                requestAccountAfterSetServer = false;
                fetchAccountInfo();
            }
        }
    }

    private class paidResultListener implements PacketListener{
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
