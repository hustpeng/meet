package org.jivesoftware.smackx.xepmodule;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.PacketListener;

import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class Xepmodule {

    private final Map<String, XepQueryInfo> pendingQueries = new ConcurrentHashMap<String, XepQueryInfo>();
    public Connection xmppConnection;

    public void clearResource() {
    }

    public void addQueryInfo(XepQueryInfo aQuery, final String key, PacketListener aPacketListener) {
        aQuery.getTimer().schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (pendingQueries) {
                    if (!pendingQueries.containsKey(key)) {
                        return;
                    }
                }

                processQueryWithFailureCode(pendingQueries.get(key), "timeout");
                removeQueryInfo(pendingQueries.get(key), key);
            }

        }, aQuery.getTimeout() * 1000);

        synchronized (pendingQueries) {
            aQuery.setQueryPacketListener(aPacketListener);
            pendingQueries.put(key, aQuery);
        }
    }

    public void processQueryWithFailureCode(XepQueryInfo aQuery, String error) {

    }

    public XepQueryInfo getQueryInfo(String key) {
        synchronized (pendingQueries) {
            return pendingQueries.get(key);
        }
    }

    public void removeQueryInfo(XepQueryInfo aQuery, String key) {
        synchronized (pendingQueries) {
            if (aQuery != null) {
                aQuery.getTimer().cancel();

                if (xmppConnection != null && aQuery.getQueryPacketListener() != null) {
                    xmppConnection.removePacketListener(aQuery.getQueryPacketListener());
                }
            }
            pendingQueries.remove(key);
        }
    }

    public void abortAllQuery() {
        synchronized (pendingQueries) {
            Object[] keysArrayList = pendingQueries.keySet().toArray();
            XepQueryInfo tempQueryInfo;
            for (int i = 0; i < keysArrayList.length; i++) {
                tempQueryInfo = pendingQueries.get(keysArrayList[i]);
                processQueryWithFailureCode(tempQueryInfo, "abort");
                removeQueryInfo(tempQueryInfo, keysArrayList[i].toString());
            }
        }
    }

    public boolean isQueryExist(int queryType, String param1, String param2) {
        synchronized (pendingQueries) {
            Object[] keysArrayList = pendingQueries.keySet().toArray();
            XepQueryInfo tempQueryInfo;
            for (int i = 0; i < keysArrayList.length; i++) {
                tempQueryInfo = pendingQueries.get(keysArrayList[i]);
                if (tempQueryInfo.getQueryType() != queryType) {
                    continue;
                }
                if (param1 != null) {
                    if (!param1.equals(tempQueryInfo.getParam1())) {
                        continue;
                    }
                }
                if (param2 != null) {
                    if (!param2.equals(tempQueryInfo.getParam2())) {
                        continue;
                    }
                }
                return true;
            }
        }
        return false;
    }
}
