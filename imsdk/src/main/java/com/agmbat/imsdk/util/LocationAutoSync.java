package com.agmbat.imsdk.util;

import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.log.Log;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smackx.location.LocateObject;

import java.util.Timer;
import java.util.TimerTask;

public class LocationAutoSync {
    private final String TAG = LocationAutoSync.class.getSimpleName();
    private final long START_INTERVAL = 1000L;
    private final long SYNC_INTERVAL = 600000L;
    private Timer syncTimer = null;
    private LocationHelper.LocationEventListener listener = new LocationHelper.LocationEventListener() {

        @Override
        public void notifyRequestLocationFailed(int errCode) {
            XMPPManager.getInstance().getLocationHelper().removeListener(this);

            Log.d(TAG, "notifyRequestLocationFailed = " + errCode);
        }

        @Override
        public void notifyRequestLocationResult(double lat, double lon) {
            Log.d(TAG, "notifyRequestLocationResult = " + lat + " " + lon);
        }

        @Override
        public void notifyRequestLocationFullResult(LocationHelper.LocationObject location) {
            Log.d(TAG, "notifyRequestLocationFullResult = " + location.lat + " " + location.lon
                    + " " + location.locationStr);

            XMPPManager.getInstance().getLocationHelper().removeListener(this);

            if (location != null) {
                LocateObject locateObject = new LocateObject();
                locateObject.setLat(location.lat);
                locateObject.setLon(location.lon);
                locateObject.setLocationStr(location.locationStr);

                XMPPManager.getInstance().getLocateManager().setLocation(locateObject);
            }
        }
    };
    private ConnectionListener myConnectionListener = new ConnectionListener() {
        @Override
        public void loginSuccessful() {
            start();
        }

        @Override
        public void connectionClosedOnError(Exception e) {
            stop();
        }

        @Override
        public void connectionClosed() {
            stop();
        }
    };

    public LocationAutoSync() {
        Connection.addConnectionCreationListener(new ConnectionCreationListener() {

            @Override
            public void connectionCreated(Connection connection) {
                connection.addConnectionListener(myConnectionListener);
            }
        });
    }

    public void start() {
        synchronized (TAG) {
            if (syncTimer != null) {
                syncTimer.cancel();
                syncTimer.purge();
                syncTimer = null;
            }

            syncTimer = new Timer("Location Sync Timer", true);
            syncTimer.schedule(new TimerTask() {
                public void run() {
                    XMPPManager.getInstance().getLocationHelper().addListener(listener);
                    XMPPManager.getInstance().getLocationHelper().requestOntimeLocation();
                }
            }, START_INTERVAL, SYNC_INTERVAL);
        }
    }

    public void stop() {
        synchronized (TAG) {
            if (syncTimer != null) {
                syncTimer.cancel();
                syncTimer.purge();
                syncTimer = null;
            }
        }
    }
}
