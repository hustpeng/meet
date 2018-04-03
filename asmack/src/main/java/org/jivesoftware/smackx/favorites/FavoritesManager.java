package org.jivesoftware.smackx.favorites;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.XmppStringUtils;
import org.jivesoftware.smackx.db.CacheStoreBase;
import org.jivesoftware.smackx.xepmodule.xepmodule;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FavoritesManager extends xepmodule {

    private static final int fetchFavorites = 0;
    private static final int addFavorite = 1;
    private static final int removeFavorite = 2;

    private CacheStoreBase<FavoritesObject> cacheStorage;

    private ArrayList<Presence> earlyPresenceList;
    private PresencePacketListener presencePacketListener;
    boolean hasFavorite = false;

    private final List<FavoritesListener> listeners;

    public FavoritesManager(final Connection connection) {
        this.xmppConnection = connection;
        Connection.addConnectionCreationListener(new ConnectionCreationListener() {

            @Override
            public void connectionCreated(Connection connection) {
                connection.addConnectionListener(myConnectionListener);
            }
        });

        listeners = new CopyOnWriteArrayList<FavoritesListener>();

        cacheStorage = new CacheStoreBase<FavoritesObject>();

        earlyPresenceList = new ArrayList<Presence>();
        presencePacketListener = new PresencePacketListener();
        xmppConnection.addPacketListener(presencePacketListener, new PacketTypeFilter(
                Presence.class));

        PacketFilter favoritesFilter = new PacketTypeFilter(FavoritesPacket.class);
        xmppConnection.addPacketListener(new FavoritesPacketListener(), favoritesFilter);
    }

    private ConnectionListener myConnectionListener = new ConnectionListener() {
        @Override
        public void loginSuccessful() {
            earlyPresenceList.clear();
            cacheStorage.cleanAllEntryForOwner();
            fetchFavorite();
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
        xmppConnection.removePacketListener(presencePacketListener);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public void addListener(FavoritesListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(FavoritesListener listener) {
        listeners.remove(listener);
    }

    private void fireFavoritesChangedEvent() {
        for (FavoritesListener listener : listeners) {
            listener.favoritesChanged();
        }
    }

    private void notifyFetchFavoritesResult(Boolean success) {
        for (FavoritesListener listener : listeners) {
            listener.notifyFetchFavoritesResult(success);
        }
    }

    private void notifyAddFavoritesResult(String jid, Boolean success) {
        for (FavoritesListener listener : listeners) {
            listener.notifyAddFavoritesResult(jid, success);
        }
    }

    private void notifyRemoveFavoritesResult(String jid, Boolean success) {
        for (FavoritesListener listener : listeners) {
            listener.notifyRemoveFavoritesResult(jid, success);
        }
    }

    @Override
    public void processQueryWithFailureCode(xepQueryInfo aQuery, String error) {
        switch (aQuery.getQueryType()) {
            case fetchFavorites:
                notifyFetchFavoritesResult(false);
                break;

            case addFavorite:
                notifyAddFavoritesResult(aQuery.getParam1(), false);
                break;

            case removeFavorite:
                notifyRemoveFavoritesResult(aQuery.getParam1(), false);
                break;

            default:
                break;
        }
    }

    private void processQueryResponse(Packet packet, xepQueryInfo queryInfo) {
        switch (queryInfo.getQueryType()) {
            case fetchFavorites: {
                if (packet.getError() == null) {
                    notifyFetchFavoritesResult(true);
                } else {
                    notifyFetchFavoritesResult(false);
                }
            }
                break;

            case addFavorite: {
                if (packet.getError() == null) {
                    notifyAddFavoritesResult(queryInfo.getParam1(), true);
                } else {
                    notifyAddFavoritesResult(queryInfo.getParam1(), false);
                }
            }
                break;

            case removeFavorite: {
                if (packet.getError() == null) {
                    notifyRemoveFavoritesResult(queryInfo.getParam1(), true);
                } else {
                    notifyRemoveFavoritesResult(queryInfo.getParam1(), false);
                }
            }
                break;

            default:
                break;
        }
    }

    public void fetchFavorite() {
        if (!xmppConnection.isAuthenticated() || xmppConnection.isAnonymous()) {
            notifyFetchFavoritesResult(false);
            return;
        }

        if (isQueryExist(fetchFavorites, null, null)) {
            return;
        }

        FavoritesPacket packet = new FavoritesPacket();
        String packetId = packet.getPacketID();
        PacketFilter idFilter = new PacketIDFilter(packetId);
        FavoritesResultListener packetListener = new FavoritesResultListener();
        xmppConnection.addPacketListener(packetListener, idFilter);

        xepQueryInfo queryInfo = new xepQueryInfo(fetchFavorites);
        addQueryInfo(queryInfo, packetId, packetListener);

        xmppConnection.sendPacket(packet);
    }

    public ArrayList<FavoritesObject> getAllFavoritesObjects() {
        return cacheStorage.getAllEntires();
    }

    public class AddFavoritesPacket extends IQ {

        private final String jid;

        public AddFavoritesPacket(String aJid) {
            // TODO Auto-generated constructor stub
            jid = aJid;
            setType(Type.SET);
        }

        public String getChildElementXML() {
            StringBuilder buf = new StringBuilder();

            buf.append("<");
            buf.append(FavoritesProvider.elementName());
            buf.append(" xmlns=\"");
            buf.append(FavoritesProvider.namespace());
            buf.append("\">");
            buf.append("<item jid=\"");
            buf.append(XmppStringUtils.escapeForXML(jid));
            buf.append("\" subscription=\"to\"/></");
            buf.append(FavoritesProvider.elementName());
            buf.append(">");

            return buf.toString();
        }
    }

    public void addFavorite(String jid) {
        if (!xmppConnection.isAuthenticated() || xmppConnection.isAnonymous()) {
            notifyAddFavoritesResult(jid, false);
            return;
        }

        if (isQueryExist(addFavorite, jid, null)) {
            return;
        }

        AddFavoritesPacket packet = new AddFavoritesPacket(XmppStringUtils.parseBareAddress(jid));
        String packetId = packet.getPacketID();
        PacketFilter idFilter = new PacketIDFilter(packetId);
        FavoritesResultListener packetListener = new FavoritesResultListener();
        xmppConnection.addPacketListener(packetListener, idFilter);

        xepQueryInfo queryInfo = new xepQueryInfo(addFavorite, jid);
        addQueryInfo(queryInfo, packetId, packetListener);

        xmppConnection.sendPacket(packet);
    }

    public class RemoveFavoritesPacket extends IQ {

        private final String jid;

        public RemoveFavoritesPacket(String aJid) {
            // TODO Auto-generated constructor stub
            jid = aJid;
            setType(Type.SET);
        }

        public String getChildElementXML() {
            StringBuilder buf = new StringBuilder();

            buf.append("<");
            buf.append(FavoritesProvider.elementName());
            buf.append(" xmlns=\"");
            buf.append(FavoritesProvider.namespace());
            buf.append("\">");
            buf.append("<item jid=\"");
            buf.append(XmppStringUtils.escapeForXML(jid));
            buf.append("\" subscription=\"remove\"/></");
            buf.append(FavoritesProvider.elementName());
            buf.append(">");
            return buf.toString();
        }
    }

    public void removeFavorite(String jid) {
        if (!xmppConnection.isAuthenticated() || xmppConnection.isAnonymous()) {
            notifyRemoveFavoritesResult(jid, false);
            return;
        }

        if (isQueryExist(removeFavorite, jid, null)) {
            return;
        }

        RemoveFavoritesPacket packet = new RemoveFavoritesPacket(XmppStringUtils.parseBareAddress(jid));
        String packetId = packet.getPacketID();
        PacketFilter idFilter = new PacketIDFilter(packetId);
        FavoritesResultListener packetListener = new FavoritesResultListener();
        xmppConnection.addPacketListener(packetListener, idFilter);

        xepQueryInfo queryInfo = new xepQueryInfo(removeFavorite, jid);
        addQueryInfo(queryInfo, packetId, packetListener);

        xmppConnection.sendPacket(packet);
    }

    public boolean isFavorite(String jid) {
        boolean result = false;
        if (cacheStorage.getAllEntires().size() <= 0) {
            return result;
        }
        for (FavoritesObject obj : cacheStorage.getAllEntires()) {
            if (obj.getJid().equals(jid)) {
                result = true;
                return result;
            }
        }
        return result;
    }

    /**
     * Listens for all presence packets and processes them.
     */
    private class PresencePacketListener implements PacketListener {

        public void processPacket(Packet packet) {
            Presence presence = (Presence)packet;

            if (!hasFavorite) {
                earlyPresenceList.add(presence);
                return;
            }

            handlePresence(presence);
        }
    }

    private void handlePresence(Presence aPresence) {
        if (aPresence.getType() == Presence.Type.available) {
            String fromString = aPresence.getFrom();
            String userKey = XmppStringUtils.parseBareAddress(fromString);
            FavoritesObject item = (FavoritesObject)cacheStorage.getEntryWithKey(userKey
                    .toLowerCase());
            if (item != null) {
                item.setOnline(true);

                cacheStorage.insertOrUpdate(item);
                fireFavoritesChangedEvent();
            }
        } else if (aPresence.getType() == Presence.Type.unavailable) {
            String fromString = aPresence.getFrom();
            String userKey = XmppStringUtils.parseBareAddress(fromString);
            FavoritesObject item = (FavoritesObject)cacheStorage.getEntryWithKey(userKey
                    .toLowerCase());
            if (item != null) {
                item.setOnline(false);

                cacheStorage.insertOrUpdate(item);
                fireFavoritesChangedEvent();
            }
        }
        // 其它情况不处理
    }

    private class FavoritesResultListener implements PacketListener {
        public void processPacket(Packet packet) {
            String packetIdString = packet.getPacketID();
            xepQueryInfo queryInfo = getQueryInfo(packetIdString);
            if (queryInfo != null) {
                removeQueryInfo(queryInfo, packetIdString);
                processQueryResponse(packet, queryInfo);
            }
        }
    }

    private class FavoritesPacketListener implements PacketListener {

        public void processPacket(Packet packet) {

            //添加和删除favorites时，favorites数据不在原IQ包的Result中
            boolean favoritesChanged = false;
            FavoritesPacket favoritesPacket = (FavoritesPacket)packet;
            for (FavoritesObject item : favoritesPacket.getFavoritesItems()) {
                if (item.getSubscription().equalsIgnoreCase("remove")) {
                    cacheStorage.delete(item);
                    favoritesChanged = true;
                } else {
                    cacheStorage.insertOrUpdate(item);
                    favoritesChanged = true;
                }
            }

            if (favoritesChanged) {
                fireFavoritesChangedEvent();
            }

            if (!hasFavorite) {
                hasFavorite = true;
                for (int i = 0; i < earlyPresenceList.size(); i++) {
                    handlePresence(earlyPresenceList.get(i));
                }
                earlyPresenceList.clear();
            }
        }
    }
}
