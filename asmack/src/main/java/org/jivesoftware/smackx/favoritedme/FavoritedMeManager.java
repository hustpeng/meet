
package org.jivesoftware.smackx.favoritedme;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.db.CacheStoreBase;
import org.jivesoftware.smackx.message.MessageRelationExtension;
import org.jivesoftware.smackx.message.MessageRelationProvider;
import org.jivesoftware.smackx.visitor.VisitorMeListener;
import org.jivesoftware.smackx.visitor.VisitorMeReadFlagObject;
import org.jivesoftware.smackx.xepmodule.xepmodule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FavoritedMeManager extends xepmodule{

    private static final int fetchFavoritedMe = 0;

    private CacheStoreBase<FavoritedMeObject> cacheStorage;
    private FavoritedMeReadFlagStorage readFlagStorage;

    private final List<FavoritedMeListener> listeners;

    private int currentFetchPage = 0;

    private static final int FAVORITED_ME_PAGE_SIZE = 20;

    public static final String FAVORITED_ME_NO_MORE = "no more";

    public FavoritedMeManager(final Connection connection) {
        this.xmppConnection = connection;
        Connection.addConnectionCreationListener(new ConnectionCreationListener() {

            @Override
            public void connectionCreated(Connection connection) {
                connection.addConnectionListener(myConnectionListener);
                connection.addPacketListener(visitorMeMsgPacketListener, visitorMeMsgPacketFilter);
            }
        });

        listeners = new CopyOnWriteArrayList<FavoritedMeListener>();

        cacheStorage = new CacheStoreBase<FavoritedMeObject>();
        readFlagStorage = new FavoritedMeReadFlagStorage();
    }

    private ConnectionListener myConnectionListener = new ConnectionListener() {
        @Override
        public void loginSuccessful() {
            cacheStorage.cleanAllEntryForOwner();
        }

        @Override
        public void connectionClosedOnError(Exception e) {
            abortAllQuery();
            xmppConnection.removePacketListener(visitorMeMsgPacketListener);
        }

        @Override
        public void connectionClosed() {
            abortAllQuery();
            xmppConnection.removePacketListener(visitorMeMsgPacketListener);
        }
    };

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public void addListener(FavoritedMeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(FavoritedMeListener listener) {
        listeners.remove(listener);
    }

    private void notifyFavoritedMeChanged() {
        for (FavoritedMeListener listener : listeners) {
            listener.notifyFavoritedMeChanged();
        }
    }

    private void notifyFetchFavoritedMeResult(Boolean success, String error) {
        for (FavoritedMeListener listener : listeners) {
            listener.notifyFetchFavoritedMeResult(success, error);
        }
    }
    private void notifyNewFavoritedMe() {
        int count = getNewFaoritedMeCount();
        for (FavoritedMeListener listener : listeners) {
            listener.notifyNewFavoritedMe(count);
        }
    }
    public int getNewFaoritedMeCount(){
        return readFlagStorage.getNewFavoritedMeCount();
    }
    public ArrayList<FavoritedMeObject> getAllFavoritedMeObjects()
    {
        ArrayList<FavoritedMeObject> arrayList = cacheStorage.getAllEntires();
        Collections.sort(arrayList);
        return arrayList;
    }

    @Override
    public void processQueryWithFailureCode(xepQueryInfo aQuery, String error)
    {
        switch (aQuery.getQueryType()) {
            case fetchFavoritedMe:
                notifyFetchFavoritedMeResult(false, null);
                break;

            default:
                break;
        }
    }

    private void processQueryResponse(Packet packet, xepQueryInfo queryInfo)
    {
        switch (queryInfo.getQueryType()) {
            case fetchFavoritedMe:
            {
                if (packet.getError() == null) {
                    boolean isChanged = false;
                    FavoritedMePacket favoritedMePacket = (FavoritedMePacket)packet;
                    for(FavoritedMeObject item : favoritedMePacket.getFavoritedMeItems()){
                        if (readFlagStorage.isReadFlagExsit(item.getJid())) {
                            item.setNew(true);
                            item.setRemoveFromReadFlag(false);
                        }
                        else {
                            item.setNew(false);
                            item.setRemoveFromReadFlag(true);
                        }
                        cacheStorage.insertOrUpdate(item);
                        isChanged = true;
                    }

                    if (isChanged) {
                        notifyFavoritedMeChanged();
                        currentFetchPage++;
                    }
                    notifyNewFavoritedMe();

                    if (favoritedMePacket.getFavoritedMeItems().size() < FAVORITED_ME_PAGE_SIZE) {
                        notifyFetchFavoritedMeResult(true, FAVORITED_ME_NO_MORE);
                    }
                    else{
                        notifyFetchFavoritedMeResult(true, null);
                    }
                }
                else {
                    notifyFetchFavoritedMeResult(false, null);
                }
            }
                break;

            default:
                break;
        }
    }

    public void addFavoritedMeReadFlag(String jidString){
        readFlagStorage.insertOrUpdateReadFlag(jidString);
        notifyNewFavoritedMe();
    }

    public void removeFavoritedMeReadFlag(String jidString){
        readFlagStorage.deleteReadFlag(jidString);
        notifyNewFavoritedMe();
    }

    public class fetchFavoritedMePacket extends IQ {
        private final int startIndex;
        private final int pageSize;

        public fetchFavoritedMePacket(int aStartIndex, int aPageSize) {
            // TODO Auto-generated constructor stub
            startIndex = aStartIndex;
            pageSize = aPageSize;
        }

        public String getChildElementXML() {
            StringBuilder buf = new StringBuilder();
            buf.append("<");
            buf.append(FavoritedMeProvider.elementName());
            buf.append(" xmlns=\"");
            buf.append(FavoritedMeProvider.namespace());
            buf.append("\"");
            buf.append(" startindex=\"");
            buf.append(startIndex);
            buf.append("\" pagesize=\"");
            buf.append(pageSize);
            buf.append("\"/>");
            return buf.toString();
        }
    }

    private PacketFilter visitorMeMsgPacketFilter = new PacketFilter() {

        @Override
        public boolean accept(Packet packet) {
            if (!(packet instanceof Message)) {
                return false;
            }
            Message.Type messageType = ((Message)packet).getType();
            return messageType == Message.Type.normal;
        }

    };

    private PacketListener visitorMeMsgPacketListener = new PacketListener() {

        @Override
        public void processPacket(Packet packet) {

            Message message = (Message)packet;
            if (("http://jabber.org/protocol/muc#verify".equals(message.getXmlns()))
                    || ("http://jabber.org/protocol/muc#admin".equals(message.getXmlns()))
                    || ("http://jabber.org/protocol/muc#hotcircle".equals(message.getXmlns()))
                    || ("http://jabber.org/protocol/muc#owner".equals(message.getXmlns()))) {
                return;
            }
            MessageRelationExtension extension = (MessageRelationExtension)message.getExtension(
                    MessageRelationProvider.elementName(), MessageRelationProvider.namespace());
            if (!"fanme".equals(extension.getType())) {
                return;
            }
            if ("add".equals(extension.getAction())) {
                addFavoritedMeReadFlag(message.getFrom());
            } else if ("remove".equals(extension.getAction())) {
                removeFavoritedMeReadFlag(message.getFrom());
            }

        }
    };

    /**
     * arraylist中是FavoritedMeObject对象
     * @return
     */
    public ArrayList<FavoritedMeObject> getFavoritedMeObjects()
    {
        return cacheStorage.getAllEntires();
    }

    public void fetchNextFavoritedMe(boolean fromStart) {
        if (!xmppConnection.isAuthenticated() || xmppConnection.isAnonymous()) {
            notifyFetchFavoritedMeResult(false, null);
            return;
        }

        if (isQueryExist(fetchFavoritedMe, null, null)) {
            return;
        }

        if (fromStart) {
            if (cacheStorage.getEntriesCount() > 0) {
                cacheStorage.cleanAllEntryForOwner();
                notifyFavoritedMeChanged();
            }
            currentFetchPage = 0;
        }

        fetchFavoritedMePacket packet = new fetchFavoritedMePacket(currentFetchPage, FAVORITED_ME_PAGE_SIZE);
        String packetId = packet.getPacketID();
        PacketFilter idFilter = new PacketIDFilter(packetId);
        FavoritedMeResultListener packetListener = new FavoritedMeResultListener();
        xmppConnection.addPacketListener(packetListener, idFilter);

        xepQueryInfo queryInfo = new xepQueryInfo(fetchFavoritedMe);
        addQueryInfo(queryInfo, packetId, packetListener);

        xmppConnection.sendPacket(packet);
    }

    private class FavoritedMeResultListener implements PacketListener{
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
