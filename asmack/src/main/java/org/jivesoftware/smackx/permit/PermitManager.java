package org.jivesoftware.smackx.permit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.db.CacheStoreBase;
import org.jivesoftware.smackx.vcard.VCardObject;
import org.jivesoftware.smackx.xepmodule.xepmodule;

public class PermitManager extends xepmodule {
    private static final int fetchPermits = 11;
    private static final int addPermit = 22;
    private static final int removePermit = 33;

    private CacheStoreBase<PermitObject> cacheStorage;

    boolean hasPermit = false;
    private final List<PermitListener> listeners;

    public PermitManager(final Connection connection) {
        this.xmppConnection = connection;
        Connection.addConnectionCreationListener(new ConnectionCreationListener() {

            @Override
            public void connectionCreated(Connection connection) {
                connection.addConnectionListener(myConnectionListener);
            }
        });
        listeners = new CopyOnWriteArrayList<PermitListener>();

        cacheStorage = new CacheStoreBase<PermitObject>();

    }

    private ConnectionListener myConnectionListener = new ConnectionListener() {
        @Override
        public void loginSuccessful() {
            cacheStorage.cleanAllEntryForOwner();
            fetchPermits();
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
    public void clearResource() {
        super.clearResource();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public void addListener(PermitListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(PermitListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void processQueryWithFailureCode(xepQueryInfo aQuery, String error) {
        switch (aQuery.getQueryType()) {
            case fetchPermits:
                notifyFetchPermitResult(false);
                break;
            case addPermit:
                notifyAddPermitResult(aQuery.getParam1(), false);
                break;
            case removePermit:
                notifyRemovePermitResult(aQuery.getParam1(), false);
                break;
            default:
                break;
        }
    }

    private void processQueryResponse(Packet packet, xepQueryInfo queryInfo) {
        switch (queryInfo.getQueryType()) {
            case fetchPermits: {
                PermitPacket permitPacket = (PermitPacket)packet;
                for (PermitObject item : permitPacket.getPermitItems()) {
                    cacheStorage.insertOrUpdate(item);
                    // notifyPermitListChange(item.getJid(),true);
                }
            }
                break;

            case addPermit: {
                VCardObject vcard = (VCardObject)queryInfo.getParam3();
                if (packet.getError() == null) {
                    PermitObject object = new PermitObject();
                    object.setJid(vcard.getJid());
                    object.setNickname(vcard.getNickname());
                    object.setAvatar(vcard.getAvatar());
                    object.setStatus(vcard.getStatus());
                    cacheStorage.insertOrUpdate(object);
                    notifyAddPermitResult(vcard.getJid(), true);
                } else {
                    notifyAddPermitResult(vcard.getJid(), false);
                }
            }
                break;

            case removePermit: {
                String jid = queryInfo.getParam1();
                if (packet.getError() == null) {
                    PermitObject object = new PermitObject();
                    object.setJid(jid);
                    cacheStorage.delete(object);
                    notifyRemovePermitResult(jid, true);
                } else {
                    notifyRemovePermitResult(jid, false);
                }
            }
                break;

            default:
                break;
        }
    }

    public ArrayList<PermitObject> getAllPermitObjects() {
        return cacheStorage.getAllEntires();
    }

    public void fetchPermits() {
        if (!xmppConnection.isAuthenticated() || xmppConnection.isAnonymous()) {
            notifyFetchPermitResult(false);
            return;
        }

        if (isQueryExist(fetchPermits, null, null)) {
            return;
        }
        PermitPacket packet = new PermitPacket();
        String packetId = packet.getPacketID();
        PacketFilter idFilter = new PacketIDFilter(packetId);
        PermitResultListener packetListener = new PermitResultListener();
        xmppConnection.addPacketListener(packetListener, idFilter);

        xepQueryInfo queryInfo = new xepQueryInfo(fetchPermits);
        addQueryInfo(queryInfo, packetId, packetListener);

        xmppConnection.sendPacket(packet);
    }

    public class AddPermitPacket extends IQ {

        private String jid;

        public AddPermitPacket(String jid) {
            this.jid = jid;
            setType(Type.SET);
        }

        @Override
        public String getChildElementXML() {
            StringBuilder buffer = new StringBuilder();

            buffer.append("<");
            buffer.append(PermitProvider.elementName());
            buffer.append(" xmlns=\"");
            buffer.append(PermitProvider.namespace());
            buffer.append("\">");
            buffer.append("<item jid=\"");
            buffer.append(StringUtils.escapeForXML(jid));
            buffer.append("\"/></query>");
            return buffer.toString();
        }

    }

    public void addPermit(VCardObject vcard) {
        if (!xmppConnection.isAuthenticated() || xmppConnection.isAnonymous()) {
            notifyAddPermitResult(vcard.getJid(), false);
            return;
        }

        if (isQueryExist(addPermit, vcard.getJid(), null)) {
            return;
        }
        AddPermitPacket packet = new AddPermitPacket(StringUtils.parseBareAddress(vcard.getJid()));
        String packetId = packet.getPacketID();
        PacketFilter idFilter = new PacketIDFilter(packetId);
        PermitResultListener packetListener = new PermitResultListener();
        xmppConnection.addPacketListener(packetListener, idFilter);

        xepQueryInfo queryInfo = new xepQueryInfo(addPermit, null, null, vcard);
        addQueryInfo(queryInfo, packetId, packetListener);

        xmppConnection.sendPacket(packet);
    }

    public class RemovePermitPacket extends IQ {

        private String jid;

        public RemovePermitPacket(String jid) {
            this.jid = jid;
            setType(Type.SET);
        }

        @Override
        public String getChildElementXML() {
            StringBuilder buffer = new StringBuilder();

            buffer.append("<");
            buffer.append(PermitProvider.elementName());
            buffer.append(" xmlns=\"");
            buffer.append(PermitProvider.namespace());
            buffer.append("\">");
            buffer.append("<item jid=\"");
            buffer.append(StringUtils.escapeForXML(jid));
            buffer.append("\" action=\"");
            buffer.append("remove");
            buffer.append("\"/></query>");
            return buffer.toString();
        }
    }

    public void removePermit(String jid) {
        if (!xmppConnection.isAuthenticated() || xmppConnection.isAnonymous()) {
            notifyRemovePermitResult(jid, false);
            return;
        }

        if (isQueryExist(removePermit, jid, null)) {
            return;
        }
        RemovePermitPacket packet = new RemovePermitPacket(StringUtils.parseBareAddress(jid));
        String packetId = packet.getPacketID();
        PacketFilter idFilter = new PacketIDFilter(packetId);
        PermitResultListener packetListener = new PermitResultListener();
        xmppConnection.addPacketListener(packetListener, idFilter);

        xepQueryInfo queryInfo = new xepQueryInfo(removePermit, jid);
        addQueryInfo(queryInfo, packetId, packetListener);

        xmppConnection.sendPacket(packet);
    }

    private class PermitResultListener implements PacketListener {
        @Override
        public void processPacket(Packet packet) {
            String packetId = packet.getPacketID();
            xepQueryInfo queryInfo = getQueryInfo(packetId);
            if (queryInfo != null) {
                removeQueryInfo(queryInfo, packetId);
                processQueryResponse(packet, queryInfo);
            }
        }
    }

    private void notifyFetchPermitResult(Boolean success) {
        for (PermitListener listener : listeners) {
            listener.notifyFetchPermitResult(success);

        }
    }

    private void notifyAddPermitResult(String jid, boolean success) {
        for (PermitListener listener : listeners) {
            listener.notifyAddPermitResult(jid, success);
        }
    }

    private void notifyRemovePermitResult(String jid, boolean success) {
        for (PermitListener listener : listeners) {
            listener.notifyRemovePermitResult(jid, success);
        }
    }

    public boolean isPermit(String jid) {
        boolean result = false;
        if (cacheStorage.getAllEntires().size() <= 0) {
            return result;
        }
        for (PermitObject obj : cacheStorage.getAllEntires()) {
            if (obj.getJid().equals(jid)) {
                result = true;
                return result;
            }
        }
        return result;
    }
}
