package org.jivesoftware.smackx.visitor;

import android.os.SystemClock;

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
import org.jivesoftware.smackx.xepmodule.xepmodule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

public class VisitorManager extends xepmodule {

    private static final int fetchVisitorMe = 0;
    private static final int sendVisitorRecord = 1;
    private Timer sendVisitorRecordTimer = null;

    // private final long SEND_VISITOR_RECORD_INTERVAL = 180000L;
    private final long SEND_VISITOR_RECORD_INTERVAL = 30000L;

    private CacheStoreBase<VisitorMeObject> cacheStorage;
    private VisitorMeReadFlagStorage readFlagStorage;
    private VisitorRecordStorage visitorRecordStorage;

    private final List<VisitorMeListener> listeners;

    private int currentFetchPage = 0;

    private static final int VISITOR_ME_PAGE_SIZE = 20;

    public VisitorManager(final Connection connection) {
        this.xmppConnection = connection;
        Connection.addConnectionCreationListener(new ConnectionCreationListener() {

            @Override
            public void connectionCreated(Connection connection) {
                connection.addConnectionListener(myConnectionListener);
                connection.addPacketListener(visitorMeMsgPacketListener, visitorMeMsgPacketFilter);
            }
        });

        listeners = new CopyOnWriteArrayList<VisitorMeListener>();

        cacheStorage = new CacheStoreBase<VisitorMeObject>();
        readFlagStorage = new VisitorMeReadFlagStorage();
        visitorRecordStorage = new VisitorRecordStorage();
    }

    private ConnectionListener myConnectionListener = new ConnectionListener() {
        @Override
        public void loginSuccessful() {
            startSendVisitorTask();
        }

        @Override
        public void connectionClosedOnError(Exception e) {
            cacheStorage.cleanAllEntryForOwner();
            abortAllQuery();
            stopSendVisitorTask();
            xmppConnection.removePacketListener(visitorMeMsgPacketListener);
        }

        @Override
        public void connectionClosed() {
            cacheStorage.cleanAllEntryForOwner();
            abortAllQuery();
            stopSendVisitorTask();
            xmppConnection.removePacketListener(visitorMeMsgPacketListener);
        }
    };

    private void startSendVisitorTask() {
        if (sendVisitorRecordTimer != null) {
            sendVisitorRecordTimer.cancel();
            sendVisitorRecordTimer.purge();
            sendVisitorRecordTimer = null;
        }

        sendVisitorRecordTimer = new Timer("Visitor Record Sync Timer", true);
        sendVisitorRecordTimer.schedule(new TimerTask() {
            public void run() {
                synchronized (visitorRecordStorage) {
                    ArrayList<VisitorRecordObject> arrayList = visitorRecordStorage
                            .getAllVisitorRecord();
                    if (arrayList.size() > 0) {
                        sendVisitorRecord(arrayList);
                    }

                    visitorRecordStorage.deleteAllVisitorRecord();
                }
            }
        }, SEND_VISITOR_RECORD_INTERVAL, SEND_VISITOR_RECORD_INTERVAL);
    }

    private void stopSendVisitorTask() {
        if (sendVisitorRecordTimer != null) {
            sendVisitorRecordTimer.cancel();
            sendVisitorRecordTimer.purge();
            sendVisitorRecordTimer = null;
        }
    }

    public void addListener(VisitorMeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(VisitorMeListener listener) {
        listeners.remove(listener);
    }

    private void notifyVisitorMeChanged() {
        for (VisitorMeListener listener : listeners) {
            listener.notifyVisitorMeChanged();
        }
    }

    private void notifyFetchVisitorMeResult(Boolean success, String error) {
        for (VisitorMeListener listener : listeners) {
            listener.notifyFetchVisitorMeResult(success, error);
        }
    }

    private void notifyNewVisitor() {
        int count = getNewVisitorCount();
        for (VisitorMeListener listener : listeners) {
            listener.notifyNewVisitor(count);
        }
    }

    public int getNewVisitorCount() {
        return readFlagStorage.getNewVisitorCount();
    }

    public ArrayList<VisitorMeObject> getAllVisitorMeObjects() {
        ArrayList<VisitorMeObject> arrayList = cacheStorage.getAllEntires();
        Collections.sort(arrayList);
        return arrayList;
    }

    @Override
    public void processQueryWithFailureCode(xepQueryInfo aQuery, String error) {
        switch (aQuery.getQueryType()) {
            case fetchVisitorMe:
                notifyFetchVisitorMeResult(false, null);
                break;

            case sendVisitorRecord:
                break;

            default:
                break;
        }
    }

    private void processQueryResponse(Packet packet, xepQueryInfo queryInfo) {
        switch (queryInfo.getQueryType()) {
            case fetchVisitorMe: {
                if (packet.getError() == null) {
                    boolean isChanged = false;
                    VisitorMePacket visitorMePacket = (VisitorMePacket) packet;
                    for (VisitorMeObject item : visitorMePacket.getVisitorMeItems()) {
                        if (readFlagStorage.isReadFlagExsit(item.getJid(), item.getEntrance())) {
                            item.setNew(true);
                            item.setRemoveFromReadFlag(false);
                        } else {
                            item.setNew(false);
                            item.setRemoveFromReadFlag(true);
                        }
                        cacheStorage.insertOrUpdate(item);
                        isChanged = true;
                    }

                    if (isChanged) {
                        notifyVisitorMeChanged();
                        currentFetchPage++;
                    }
                    notifyNewVisitor();
                    if (visitorMePacket.getVisitorMeItems().size() < VISITOR_ME_PAGE_SIZE) {
                        notifyFetchVisitorMeResult(true, "no more");
                    } else {
                        notifyFetchVisitorMeResult(true, null);
                    }
                } else {
                    notifyFetchVisitorMeResult(false, null);
                }
            }
            break;

            case sendVisitorRecord:
                break;

            default:
                break;
        }
    }

    /**
     * 会操作数据库
     *
     * @param whoVisitorJid
     * @param visitorWhoJid
     * @param entrance
     */
    public void insertVisitorRecord(String whoVisitorJid, String visitorWhoJid, String entrance) {
        synchronized (visitorRecordStorage) {
            VisitorRecordObject newObject = new VisitorRecordObject();
            newObject.setWhoVisitorJid(whoVisitorJid);
            newObject.setVisitorWhoJid(visitorWhoJid);
            newObject.setEntrance(entrance);
            newObject.setVisitorTime(SystemClock.currentThreadTimeMillis());

            visitorRecordStorage.insertOrUpdateVisitorRecord(newObject);
        }
    }

    public void addVisitorMeReadFlag(VisitorMeReadFlagObject obj) {
        readFlagStorage.insertReadFlag(obj);
        notifyNewVisitor();
    }

    public void removeVisitorMeReadFlag(String jidString, String entrance) {
        readFlagStorage.deleteReadFlag(jidString, entrance);
        notifyNewVisitor();
    }

    /**
     * arraylist中是VisitorMeObject对象
     *
     * @return
     */
    public ArrayList<VisitorMeObject> getVisitorMeObjects() {
        return cacheStorage.getAllEntires();
    }

    public class fetchVisitorMePacket extends IQ {
        private final int startIndex;
        private final int pageSize;

        public fetchVisitorMePacket(int aStartIndex, int aPageSize) {
            // TODO Auto-generated constructor stub
            startIndex = aStartIndex;
            pageSize = aPageSize;
        }

        public String getChildElementXML() {
            StringBuilder buf = new StringBuilder();
            buf.append("<");
            buf.append(VisitorMeProvider.elementName());
            buf.append(" xmlns=\"");
            buf.append(VisitorMeProvider.namespace());
            buf.append("\"");
            buf.append(" startindex=\"");
            buf.append(startIndex);
            buf.append("\" pagesize=\"");
            buf.append(pageSize);
            buf.append("\"/>");
            return buf.toString();
        }
    }

    public void fetchNextVisitordMe(boolean fromStart) {
        if (!xmppConnection.isAuthenticated() || xmppConnection.isAnonymous()) {
            notifyFetchVisitorMeResult(false, null);
            return;
        }

        if (isQueryExist(fetchVisitorMe, null, null)) {
            return;
        }

        if (fromStart) {
            if (cacheStorage.getEntriesCount() > 0) {
                cacheStorage.cleanAllEntryForOwner();
                notifyVisitorMeChanged();
            }
            currentFetchPage = 0;
        }

        fetchVisitorMePacket packet = new fetchVisitorMePacket(currentFetchPage,
                VISITOR_ME_PAGE_SIZE);
        String packetId = packet.getPacketID();
        PacketFilter idFilter = new PacketIDFilter(packetId);
        VisitorMeResultListener packetListener = new VisitorMeResultListener();
        xmppConnection.addPacketListener(packetListener, idFilter);

        xepQueryInfo queryInfo = new xepQueryInfo(fetchVisitorMe);
        addQueryInfo(queryInfo, packetId, packetListener);

        xmppConnection.sendPacket(packet);
    }

    private PacketFilter visitorMeMsgPacketFilter = new PacketFilter() {

        @Override
        public boolean accept(Packet packet) {
            if (!(packet instanceof Message)) {
                return false;
            }
            Message.Type messageType = ((Message) packet).getType();
            return messageType == Message.Type.normal;
        }

    };
    private PacketListener visitorMeMsgPacketListener = new PacketListener() {

        @Override
        public void processPacket(Packet packet) {

            Message message = (Message) packet;
            if (("http://jabber.org/protocol/muc#verify".equals(message.getXmlns()))
                    || ("http://jabber.org/protocol/muc#admin".equals(message.getXmlns()))
                    || ("http://jabber.org/protocol/muc#hotcircle".equals(message.getXmlns()))
                    || ("http://jabber.org/protocol/muc#owner".equals(message.getXmlns()))) {
                return;
            }
            MessageRelationExtension extension = (MessageRelationExtension) message.getExtension(
                    MessageRelationProvider.elementName(), MessageRelationProvider.namespace());
            if (!"visitor".equals(extension.getType())) {
                return;
            }
            VisitorMeReadFlagObject newObject = new VisitorMeReadFlagObject();
            newObject.setVisitorJid(message.getFrom());
            newObject.setMyJid(message.getTo());
            newObject.setEntrance(extension.getEntrance());
            newObject.setCreate_date(System.currentTimeMillis());
            addVisitorMeReadFlag(newObject);
        }
    };

    public class sendVisitorRecordPacket extends IQ {
        private final ArrayList<VisitorRecordObject> sendArray;

        public sendVisitorRecordPacket(ArrayList<VisitorRecordObject> arrayList) {
            sendArray = arrayList;
            setType(Type.SET);// add by jack
        }

        public String getChildElementXML() {
            StringBuilder buf = new StringBuilder();
            buf.append("<");
            buf.append(VisitorMeProvider.elementName());
            buf.append(" xmlns=\"");
            buf.append(VisitorMeProvider.namespace());
            buf.append("\">");
            for (VisitorRecordObject object : sendArray) {
                buf.append(object);
            }
            buf.append("</");
            buf.append(VisitorMeProvider.elementName());
            buf.append(">");

            return buf.toString();
        }
    }

    private void sendVisitorRecord(ArrayList<VisitorRecordObject> array) {
        if (array.size() <= 0) {
            return;
        }

        if (!xmppConnection.isAuthenticated() || xmppConnection.isAnonymous()) {
            return;
        }

        if (isQueryExist(sendVisitorRecord, null, null)) {
            return;
        }

        sendVisitorRecordPacket packet = new sendVisitorRecordPacket(array);
        String packetId = packet.getPacketID();

        xepQueryInfo queryInfo = new xepQueryInfo(fetchVisitorMe);
        addQueryInfo(queryInfo, packetId, null);

        xmppConnection.sendPacket(packet);
    }

    private class VisitorMeResultListener implements PacketListener {
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
