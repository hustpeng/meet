
package org.jivesoftware.smackx.xepmodule;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.PacketListener;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class xepmodule {

    public class xepQueryInfo
    {
        private int queryType;
        private int timeout;
        private String param1;
        private String param2;
        private Object param3;
        private Timer timer;
        private PacketListener queryPacketListener;

        public xepQueryInfo(int aType) {
            // TODO Auto-generated constructor stub
            this(aType, null, null);
        }

        public xepQueryInfo(int aType, String aParam1) {
            // TODO Auto-generated constructor stub
            this(aType, aParam1, null, null);
        }

        public xepQueryInfo(int aType, String aParam1, String aParam2) {
            // TODO Auto-generated constructor stub
            this(aType, aParam1, aParam2, null);
        }

        public xepQueryInfo(int aType, String aParam1, String aParam2, Object param3) {
            // TODO Auto-generated constructor stub
            setQueryType(aType);
            setParam1(aParam1);
            setParam2(aParam2);
            setParam3(param3);
            setTimeout(30);

            setTimer(new Timer());
        }

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public int getQueryType() {
            return queryType;
        }

        public void setQueryType(int queryType) {
            this.queryType = queryType;
        }

        public String getParam1() {
            return param1;
        }

        public void setParam1(String param1) {
            this.param1 = param1;
        }

        public String getParam2() {
            return param2;
        }

        public void setParam2(String param2) {
            this.param2 = param2;
        }

        public Timer getTimer() {
            return timer;
        }

        public void setTimer(Timer timer) {
            this.timer = timer;
        }

        public PacketListener getQueryPacketListener() {
            return queryPacketListener;
        }

        public void setQueryPacketListener(PacketListener queryPacketListener) {
            this.queryPacketListener = queryPacketListener;
        }

        public Object getParam3() {
            return param3;
        }

        public void setParam3(Object param3) {
            this.param3 = param3;
        }
    }

    public void clearResource()
    {

    }

    private final Map<String, xepQueryInfo> pendingQueries = new ConcurrentHashMap<String,xepQueryInfo>();
    public Connection xmppConnection;

    public void addQueryInfo(xepQueryInfo aQuery, final String key, PacketListener aPacketListener)
    {
        aQuery.getTimer().schedule(new TimerTask(){
            @Override
            public void run() {
                // TODO Auto-generated method stub
                synchronized (pendingQueries) {
                    if (!pendingQueries.containsKey(key)) {
                        return;
                    }
                }

                processQueryWithFailureCode(pendingQueries.get(key), "timeout");
                removeQueryInfo(pendingQueries.get(key), key);
            }

        }, aQuery.getTimeout()*1000);

        synchronized (pendingQueries) {
            aQuery.setQueryPacketListener(aPacketListener);
            pendingQueries.put(key, aQuery);
        }
    }

    public void processQueryWithFailureCode(xepQueryInfo aQuery, String error)
    {

    }

    public xepQueryInfo getQueryInfo(String key)
    {
        synchronized (pendingQueries) {
             return pendingQueries.get(key);
        }
    }

    public void removeQueryInfo(xepQueryInfo aQuery, String key)
    {
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

    public void abortAllQuery()
    {
        synchronized (pendingQueries) {
            Object[] keysArrayList = pendingQueries.keySet().toArray();
            xepQueryInfo tempQueryInfo;
            for (int i = 0; i < keysArrayList.length; i++) {
                tempQueryInfo = pendingQueries.get(keysArrayList[i]);

                processQueryWithFailureCode(tempQueryInfo, "abort");
                removeQueryInfo(tempQueryInfo, keysArrayList[i].toString());
            }
        }
    }

    public boolean isQueryExist(int queryType, String param1, String param2)
    {
        synchronized (pendingQueries) {
            Object[] keysArrayList = pendingQueries.keySet().toArray();
            xepQueryInfo tempQueryInfo;
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
