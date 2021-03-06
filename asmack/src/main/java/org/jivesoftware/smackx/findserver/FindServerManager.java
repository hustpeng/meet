package org.jivesoftware.smackx.findserver;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.XmppStringUtils;
import org.jivesoftware.smackx.xepmodule.XepQueryInfo;
import org.jivesoftware.smackx.xepmodule.Xepmodule;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FindServerManager extends Xepmodule {

    private final static int findServer = 0;
    //private final static int setMyVCardExtend = 1;

    private final List<FindServerListener> findServerListeners;
    private ConnectionListener myConnectionListener = new ConnectionListener() {
        @Override
        public void loginSuccessful() {
            findServer();
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

    public FindServerManager(final Connection connection) {
        this.xmppConnection = connection;
        Connection.addConnectionCreationListener(new ConnectionCreationListener() {

            @Override
            public void connectionCreated(Connection connection) {
                connection.addConnectionListener(myConnectionListener);
            }
        });
        findServerListeners = new CopyOnWriteArrayList<FindServerListener>();
    }

    public void addListener(FindServerListener findServerListener) {
        if (!findServerListeners.contains(findServerListener)) {
            findServerListeners.add(findServerListener);
        }
    }

    public void removeListener(FindServerListener findServerListener) {
        findServerListeners.remove(findServerListener);
    }

    private void notifyFindServersResult(FindServerObject serverObject) {
        for (FindServerListener listener : findServerListeners) {
            listener.notifyFindServersResult(serverObject);
        }
    }

    @Override
    public void processQueryWithFailureCode(XepQueryInfo queryInfo, String error) {
        switch (queryInfo.getQueryType()) {
            case findServer:
                notifyFindServersResult(null);
                break;

//            case setMyVCardExtend:
//                //notifySetMyVCardExtendResult(false);
//                break;

            default:
                break;
        }
    }

    private void processQueryResponse(Packet packet, XepQueryInfo queryInfo) {
        switch (queryInfo.getQueryType()) {
            case findServer: {
                if (packet.getError() == null) {
                    FindServerObject serverObject = ((FindServerPacket) packet).getFindServerObject();
                    notifyFindServersResult(serverObject);
                } else {
                    notifyFindServersResult(null);
                }
            }
            break;

//            case setMyVCardExtend:
//            {
//                if (packet.getError() == null) {
//
//                }
//                else {
//                    notifySetMyVCardExtendResult(false);
//                }
//            }
//                break;

            default:
                break;
        }
    }

    public void findServer() {
        if (!xmppConnection.isConnected()) {
            return;
        }
        if (isQueryExist(findServer, null, null)) {
            return;
        }
        FindServersPacket packet = new FindServersPacket(xmppConnection.getServiceName());
        String packetId = packet.getPacketID();
        PacketFilter idFilter = new PacketIDFilter(packetId);
        FindServerResultListener packetListener = new FindServerResultListener();
        xmppConnection.addPacketListener(packetListener, idFilter);
        XepQueryInfo queryInfo = new XepQueryInfo(findServer);
        addQueryInfo(queryInfo, packetId, packetListener);
        xmppConnection.sendPacket(packet);
    }

    public void sendReport(String jid, String reportCategory, String reportContent) {
        if (!xmppConnection.isAuthenticated() || xmppConnection.isAnonymous()) {
            return;
        }
        if (isQueryExist(1, jid, null)) {
            return;
        }
        SendReportPacket packet = new SendReportPacket(XmppStringUtils.parseBareAddress(jid),
                reportCategory, reportContent);
        String packetId = packet.getPacketID();
        PacketFilter idFilter = new PacketIDFilter(packetId);
        sendReportResultListener packetListener = new sendReportResultListener();
        xmppConnection.addPacketListener(packetListener, idFilter);

        XepQueryInfo queryInfo = new XepQueryInfo(1, jid);
        addQueryInfo(queryInfo, packetId, packetListener);

        xmppConnection.sendPacket(packet);
    }

    private class FindServerResultListener implements PacketListener {
        public void processPacket(Packet packet) {
            String packetIdString = packet.getPacketID();
            XepQueryInfo queryInfo = getQueryInfo(packetIdString);
            if (queryInfo != null) {
                removeQueryInfo(queryInfo, packetIdString);
                processQueryResponse(packet, queryInfo);
            }
        }
    }

    private class sendReportResultListener implements PacketListener {

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
