
package org.jivesoftware.smackx.findserver;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.xepmodule.xepmodule;
import org.jivesoftware.smackx.xepmodule.xepmodule.xepQueryInfo;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FindServerManager extends xepmodule{
    private final static int findServer = 0;
    //private final static int setMyVCardExtend = 1;

    private final List<FindServerListener> findServerListeners;

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

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public void addListener(FindServerListener findServerListener) {
        if (!findServerListeners.contains(findServerListener)) {
            findServerListeners.add(findServerListener);
        }
    }

    public void removeListener(FindServerListener findServerListener) {
        findServerListeners.remove(findServerListener);
    }

    private void notifyFindServersResult(FindServerObject serverObject)
    {
        for (FindServerListener listener : findServerListeners) {
            listener.notifyFindServersResult(serverObject);
        }
    }

    @Override
    public void processQueryWithFailureCode(xepQueryInfo queryInfo, String error)
    {
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

    private void processQueryResponse(Packet packet, xepQueryInfo queryInfo)
    {
        switch (queryInfo.getQueryType()) {
            case findServer:
            {
                if (packet.getError() == null) {
                    FindServerObject serverObject = ((FindServerPacket)packet).getFindServerObject();
                    notifyFindServersResult(serverObject);
                }
                else {
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

    public class findServersPacket extends IQ {

        public findServersPacket() {
            setTo(xmppConnection.getHost());
        }

        public String getChildElementXML() {
            return new StringBuffer()
                    .append("<")
                    .append(FindServerProvider.elementName())
                    .append(" xmlns=\"")
                    .append(FindServerProvider.namespace())
                    .append("\"/>")
                    .toString();
        }
    }

    public void findServer() {
        if (!xmppConnection.isConnected()) {
            return;
        }

        if (isQueryExist(findServer, null, null)) {
            return;
        }

        findServersPacket packet = new findServersPacket();

        String packetId = packet.getPacketID();
        PacketFilter idFilter = new PacketIDFilter(packetId);
        FindServerResultListener packetListener = new FindServerResultListener();
        xmppConnection.addPacketListener(packetListener, idFilter);

        xepQueryInfo queryInfo = new xepQueryInfo(findServer);
        addQueryInfo(queryInfo, packetId, packetListener);

        xmppConnection.sendPacket(packet);
    }

    private class FindServerResultListener implements PacketListener{
        public void processPacket(Packet packet) {
            String packetIdString = packet.getPacketID();
            xepQueryInfo queryInfo = getQueryInfo(packetIdString);
            if (queryInfo != null) {
                removeQueryInfo(queryInfo, packetIdString);
                processQueryResponse(packet, queryInfo);
            }
        }
    }


    // /////////////////////////////sendReportPacket/////////////////////////////////
    public class sendReportPacket extends IQ {

        private String jid;
        private String category;
        private String content;

        public sendReportPacket(String jid,String category,String content){
            this.jid = jid;
            this.category = category;
            this.content = content;
            setType(Type.SET);
        }

/*
 <iq id="HTi3o-11" type="set">
<query xmlns="jabber:iq:report">
<item jid="aaa\40email.com@10.2.7.139" reportcategory="Fake photo" reportcontent="Fake photo"/>
</query>
</iq>
 */       @Override
        public String getChildElementXML() {
            StringBuilder buf = new StringBuilder();
            buf.append("<");
            buf.append("query");
            buf.append(" xmlns=\"");
            buf.append("jabber:iq:report");
            buf.append("\">");
            buf.append("<item jid=\"");
            buf.append(StringUtils.escapeForXML(jid));
            buf.append("\" reportcategory=\"");
            buf.append(category);
            buf.append("\" reportcontent=\"");
            buf.append(content);
            buf.append("\"/></query>");
            buf.toString();
            return buf.toString();
        }

    }

    public void sendReport(String jid, String reportCategory, String reportContent) {
        if (!xmppConnection.isAuthenticated() || xmppConnection.isAnonymous()) {
            return;
        }
        if (isQueryExist(1, jid, null)) {
            return;
        }
        sendReportPacket packet = new sendReportPacket(StringUtils.parseBareAddress(jid),
                reportCategory, reportContent);
        String packetId = packet.getPacketID();
        PacketFilter idFilter = new PacketIDFilter(packetId);
        sendReportResultListener packetListener = new sendReportResultListener();
        xmppConnection.addPacketListener(packetListener, idFilter);

        xepQueryInfo queryInfo = new xepQueryInfo(1, jid);
        addQueryInfo(queryInfo, packetId, packetListener);

        xmppConnection.sendPacket(packet);
    }

    private class sendReportResultListener implements PacketListener {
        public void processPacket(Packet packet) {
            String packetIdString = packet.getPacketID();
            xepQueryInfo queryInfo = getQueryInfo(packetIdString);
            if (queryInfo != null) {
                removeQueryInfo(queryInfo, packetIdString);
                processQueryResponse(packet, queryInfo);
            }
        }
    }
    // /////////////////////////////sendReportPacket/////////////////////////////////
}
