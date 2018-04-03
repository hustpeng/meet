
package org.jivesoftware.smackx.location;

import android.text.TextUtils;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.db.CacheStoreBase;
import org.jivesoftware.smackx.xepmodule.XepQueryInfo;
import org.jivesoftware.smackx.xepmodule.Xepmodule;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LocateManager extends Xepmodule {
    /**
     * location刷新时间 in millisecond
     */
    private final static int LOCATION_REFRESH_TIME = 600000;

    private final static int getLocation = 0;
    private final static int setLocation = 1;

    private CacheStoreBase<LocateObject> cacheStorage;

    private final List<LocateListener> listeners;

    public LocateManager(final Connection connection) {
        this.xmppConnection = connection;
        Connection.addConnectionCreationListener(new ConnectionCreationListener() {

            @Override
            public void connectionCreated(Connection connection) {
                connection.addConnectionListener(myConnectionListener);
            }
        });
        listeners = new CopyOnWriteArrayList<LocateListener>();
        cacheStorage = new CacheStoreBase<LocateObject>();
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

    public void addListener(LocateListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(LocateListener listener) {
        listeners.remove(listener);
    }

    private void notifyGetLocationResult(String jid, LocateObject location) {
        for (LocateListener listener : listeners) {
            listener.notifyGetLocationResult(jid, location);
        }
    }

    private void notifySetLocationResult(boolean result) {
        for (LocateListener listener : listeners) {
            listener.notifySetLocationResult(result);
        }
    }

    @Override
    public void processQueryWithFailureCode(XepQueryInfo queryInfo, String error) {
        switch (queryInfo.getQueryType()) {
            case getLocation:
                notifyGetLocationResult(queryInfo.getParam1(), null);
                break;

            case setLocation:
                notifySetLocationResult(false);
                break;

            default:
                break;
        }
    }

    private void processQueryResponse(Packet packet, XepQueryInfo queryInfo) {
        switch (queryInfo.getQueryType()) {
            case getLocation: {
                if (packet.getError() == null) {
                    LocateObject object = ((LocatePacket) packet).getObject();
                    if (object != null) {
                        object.setJid(packet.getFrom());
                        object.setUpdate_date(new Date());
                        cacheStorage.insertOrUpdate(object);
                    }

                    notifyGetLocationResult(queryInfo.getParam1(), object);
                } else {
                    notifyGetLocationResult(queryInfo.getParam1(), null);
                }
            }
            break;

            case setLocation:
                if (packet.getError() == null) {
                    notifySetLocationResult(true);
                } else {
                    notifySetLocationResult(false);
                }

                break;

            default:
                break;
        }
    }

    private class setLocationPacket extends IQ {
        private final LocateObject location;

        public setLocationPacket(LocateObject aLocation) {
            location = aLocation;
            setType(Type.SET);
        }

        public String getChildElementXML() {
            return LocateObject.getXmlNode(location);
        }
    }

    public void setLocation(LocateObject location) {
        if (location == null) {
            notifySetLocationResult(false);
            return;
        }

        if (!xmppConnection.isAuthenticated() || xmppConnection.isAnonymous()) {
            notifySetLocationResult(false);
            return;
        }

        setLocationPacket packet = new setLocationPacket(location);

        String packetId = packet.getPacketID();
        PacketFilter idFilter = new PacketIDFilter(packetId);
        LocationResultListener packetListener = new LocationResultListener();
        xmppConnection.addPacketListener(packetListener, idFilter);

        XepQueryInfo queryInfo = new XepQueryInfo(setLocation);
        addQueryInfo(queryInfo, packetId, packetListener);

        xmppConnection.sendPacket(packet);
    }

    private class getLocationPacket extends IQ {

        public getLocationPacket(String jid) {
            setTo(jid);
        }

        public String getChildElementXML() {
            return new StringBuffer()
                    .append("<")
                    .append(LocateProvider.elementName())
                    .append(" xmlns=\"")
                    .append(LocateProvider.namespace())
                    .append("\"/>")
                    .toString();
        }
    }

    /**
     * @param jid 必须传bare jid,不能带resource
     * @return 返回LocationObject; 如果返回null, 则会异步去服务器获取,然后通过notifyGetLocationResult返回结果
     * 如果当前没有登录，notifyGetLocationResult(jid, null)会在函数返回前被调用。
     */
    public LocateObject getLocation(String jid) {
        if (TextUtils.isEmpty(jid)) {
            return null;
        }

        if (!xmppConnection.isAuthenticated() || xmppConnection.isAnonymous()) {
            notifyGetLocationResult(jid, null);
            return null;
        }

        LocateObject location = (LocateObject) cacheStorage.getEntryWithKey(jid.toLowerCase());
        if (location != null) {
            //location还没有过期
            Date lastUpdateDate = location.getUpdate_date();
            if (lastUpdateDate != null) {
                if (lastUpdateDate.after(new Date(System.currentTimeMillis() - LOCATION_REFRESH_TIME))) {
                    return location;
                }
            }
        }

        if (isQueryExist(getLocation, jid, null)) {
            return null;
        }

        getLocationPacket packet = new getLocationPacket(jid);

        String packetId = packet.getPacketID();
        PacketFilter idFilter = new PacketIDFilter(packetId);
        LocationResultListener packetListener = new LocationResultListener();
        xmppConnection.addPacketListener(packetListener, idFilter);

        XepQueryInfo queryInfo = new XepQueryInfo(getLocation, jid);
        addQueryInfo(queryInfo, packetId, packetListener);

        xmppConnection.sendPacket(packet);
        return null;
    }

    private class LocationResultListener implements PacketListener {
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
