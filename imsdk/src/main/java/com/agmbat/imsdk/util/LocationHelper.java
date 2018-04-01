package com.agmbat.imsdk.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.agmbat.android.AppResources;
import com.agmbat.log.Log;
import com.agmbat.net.HttpRequester;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

public class LocationHelper {

    private static final String TAG = LocationHelper.class.getSimpleName();

    private final LocationManager mLocationManager;
    private LocationObject lastLocation = null;
    private boolean isLocationRequesting;

    private final long UPDATE_INTERVAL = 1000L;
    private final float UPDATE_DISTANCE = 100F;

    private static final int MSG_START_LOCATION = 0;
    private static final int MSG_GET_ADDRESS = 1;

    public static final int LOCATION_GET_SUCCED = 0;
    public static final int LOCATION_SERVICE_NOT_OPEN = 1;
    public static final int LOCATION_REQUEST_UNKNOWN_ERR = 2;

    public class LocationObject {
        public String locationStr;
        public double lat;
        public double lon;
    }

    private final List<LocationEventListener> listeners;

    public interface LocationEventListener {

        /**
         * 获取地理位置失败
         *
         * @param errCode LOCATION_SERVICE_NOT_OPEN LOCATION_REQUEST_UNKNOWN_ERR
         */
        public void notifyRequestLocationFailed(int errCode);

        /**
         * 通知定位到的经纬度，地址信息会在notifyRequestLocationResultStr中通知
         *
         * @param lat
         * @param lon
         */
        public void notifyRequestLocationResult(double lat, double lon);

        /**
         * 通知定位到的经纬度以及解析到的地址信息
         *
         * @param location
         */
        public void notifyRequestLocationFullResult(LocationObject location);
    }

    public LocationHelper() {
        Context context = AppResources.getAppContext();
        listeners = new CopyOnWriteArrayList<LocationEventListener>();
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        isLocationRequesting = false;
        HandlerThread userLocationThread = new HandlerThread("Location Helper Thread");
        userLocationThread.start();

        mHandler = new Handler(userLocationThread.getLooper()) {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_START_LOCATION:
                        startLocation();
                        break;

                    case MSG_GET_ADDRESS:
                        GetAddressAndNotify((Location) msg.obj);
                        break;
                    default:
                        break;
                }
            }
        };

    }

    @SuppressLint("MissingPermission")
    private void startLocation() {
        List<String> providers = mLocationManager.getProviders(true);
        // 同时使用两个定位
        try {
            Boolean hasProvider = false;
            if (null != providers
                    && providers.contains(LocationManager.NETWORK_PROVIDER)) {
                mLocationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, UPDATE_INTERVAL,
                        UPDATE_DISTANCE, mLocationListener, Looper.myLooper());
                hasProvider = true;
            }
            if (null != providers
                    && providers.contains(LocationManager.GPS_PROVIDER)) {
                mLocationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, UPDATE_INTERVAL,
                        UPDATE_DISTANCE, mLocationListener, Looper.myLooper());
                hasProvider = true;
            }
            if (!hasProvider) {
                notifyRequestLocationFailed(LOCATION_REQUEST_UNKNOWN_ERR);
            } else {
                // 设置30秒超时
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        stopUpdatingLocation();
                        notifyCurrentLocationAfterTimeout();
                    }
                }, 30000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            notifyRequestLocationFailed(LOCATION_REQUEST_UNKNOWN_ERR);
        }
    }

    public void addListener(LocationEventListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(LocationEventListener listener) {
        listeners.remove(listener);
    }

    private void notifyRequestLocationFailed(int errCode) {
        isLocationRequesting = false;
        for (LocationEventListener listener : listeners) {
            listener.notifyRequestLocationFailed(errCode);
        }
    }

    private void notifyRequestLocationResult(double lat, double lon) {
        for (LocationEventListener listener : listeners) {
            listener.notifyRequestLocationResult(lat, lon);
        }
    }

    private void notifyRequestLocationFullResult(LocationObject location) {
        isLocationRequesting = false;
        for (LocationEventListener listener : listeners) {
            listener.notifyRequestLocationFullResult(location);
        }
    }

    public LocationObject getLastLocation() {
        if (lastLocation == null) {
            Location location = getLastLocation(mLocationManager);
            if (location != null) {
                lastLocation = new LocationObject();
                lastLocation.lat = location.getLatitude();
                lastLocation.lon = location.getLongitude();
            }
        }

        return lastLocation;
    }

    /**
     * 启动定位获取当前的地理位置
     *
     * @param context
     * @param listener
     */
    public void requestOntimeLocation() {
        if (!com.agmbat.android.utils.LocationHelper.isLocationProviderEnable(AppResources.getAppContext())) {
            notifyRequestLocationFailed(LOCATION_SERVICE_NOT_OPEN);
            return;
        }

        startRequestLocation();
    }

    /**
     * 获取最近一次定位的地理位置，如果不存在，则启动定位
     *
     * @param context
     * @param listener
     */
    public void requestLastLocation() {

//        谷歌地图：40.0425903950,116.2720591811
//        百度地图：40.0489380000,116.2784350000
//        腾讯高德：40.0426028776,116.2720735020
//        图吧地图：40.0415465276,116.2613434820
//        谷歌地球：40.0413665276,116.2660134820
//        北纬N40°02′28.92″ 东经E116°15′57.65″
        lastLocation = new LocationObject();
        lastLocation.lat = 40.0425903950;
        lastLocation.lon = 116.2720591811;
        lastLocation.locationStr = "test";
        if (lastLocation != null) {
            notifyRequestLocationResult(lastLocation.lat, lastLocation.lon);
            return;
        }

        Location location = getLastLocation(mLocationManager);
        if (location != null) {
            notifyRequestLocationResult(location.getLatitude(), location.getLongitude());
            return;
        }

        // 本地和last location都没有数据，启动定位
//        if (!com.agmbat.android.utils.LocationHelper.isLocationProviderEnable(AppResources.getAppContext())) {
//            notifyRequestLocationFailed(LOCATION_SERVICE_NOT_OPEN);
//            return;
//        }

        startRequestLocation();
    }

    public void startRequestLocation() {
        if (isLocationRequesting) {
            return;
        }

        isLocationRequesting = true;
        Message msg = mHandler.obtainMessage(MSG_START_LOCATION);
        mHandler.sendMessage(msg);
    }

    private Handler mHandler;

    /**
     * 定位超时，尝试使用last location
     */
    private void notifyCurrentLocationAfterTimeout() {
        Location location = getLastLocation(mLocationManager);
        if (location == null) {
            if (!com.agmbat.android.utils.LocationHelper.isLocationProviderEnable(AppResources.getAppContext())) {
                notifyRequestLocationFailed(LOCATION_SERVICE_NOT_OPEN);
                return;
            } else {
                notifyRequestLocationFailed(LOCATION_REQUEST_UNKNOWN_ERR);
                return;
            }
        } else {
            Message msg = mHandler.obtainMessage(MSG_GET_ADDRESS, location);
            mHandler.sendMessage(msg);
        }
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            Log.i(TAG, "onStatusChanged");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i(TAG, "onProviderEnabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.i(TAG, "onProviderDisabled");
            stopUpdatingLocation();

            notifyRequestLocationFailed(LOCATION_SERVICE_NOT_OPEN);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, "onLocationChanged = " + location);
            stopUpdatingLocation();

            Message msg = mHandler.obtainMessage(MSG_GET_ADDRESS, location);
            mHandler.sendMessage(msg);
        }
    };

    /**
     * 可能会比较耗时，不能在UI thread中调用
     *
     * @param location
     */
    private void GetAddressAndNotify(Location location) {
        if (location == null) {
            notifyRequestLocationFailed(LOCATION_REQUEST_UNKNOWN_ERR);
            return;
        }

        lastLocation = new LocationObject();
        lastLocation.lat = location.getLatitude();
        lastLocation.lon = location.getLongitude();

        notifyRequestLocationResult(lastLocation.lat, lastLocation.lon);

        lastLocation.locationStr = getDetailAddress(lastLocation.lat, lastLocation.lon);
        notifyRequestLocationFullResult(lastLocation);
    }

    public void stopUpdatingLocation() {
        try {
            mLocationManager.removeUpdates(mLocationListener);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    protected void finalize() throws Throwable {
        stopUpdatingLocation();
        super.finalize();
    }


    private static final int LOCATION_MAX_RESULT = 5;

    /**
     * @param lat
     * @param lon
     * @return address
     */
    @TargetApi(9)
    private String getDetailAddress(final double latitude, final double longitude) {
        Boolean isPresent = true;
        if (android.os.Build.VERSION.SDK_INT > 8) {
            isPresent = Geocoder.isPresent();
        }

        if (isPresent) {
            try {
                Context context = AppResources.getAppContext();
                final Geocoder geocoder = new Geocoder(context, Locale.US);
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude,
                        LOCATION_MAX_RESULT);
                if (null != addresses && addresses.size() > 0) {
                    Address address = addresses.get(0);
                    String localityName = "";
                    localityName = address.getLocality();
                    if (TextUtils.isEmpty(localityName)) {
                        localityName = address.getSubAdminArea();
                    }

                    if (TextUtils.isEmpty(localityName)) {
                        localityName = address.getAdminArea();
                    }

                    Log.i(TAG, "address: " + address);
                    return localityName;
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }

        return httpReverseGeocode(latitude, longitude);
    }

    private String httpReverseGeocode(final double lat, final double lon) {
        String localityName = "";
        try {
            String url = "http://maps.googleapis.com/maps/api/geocode/json?latlng="
                    + Double.toString(lat) + "," + Double.toString(lon) + "&sensor=false";
            HttpRequester.Builder builder = new HttpRequester.Builder();
            builder.url(url);
            builder.connectionTimeout(5000);
            builder.soTimeout(10000);
            String responseData = builder.build().requestAsString();
            JSONObject jsonObject = new JSONObject(responseData);
            if (jsonObject != null) {
                String locality = "";
                String administrative_area_level_2 = "";
                String administrative_area_level_1 = "";

                JSONArray addresses = (JSONArray) ((JSONArray) jsonObject.get("results"))
                        .getJSONObject(0).get("address_components");
                for (int i = 0; i < addresses.length(); i++) {
                    JSONObject item = addresses.getJSONObject(i);
                    if (item != null) {
                        JSONArray types = item.getJSONArray("types");
                        if (types != null && types.length() > 0) {
                            for (int j = 0; j < types.length(); j++) {
                                if ("locality".equals(types.get(j))) {
                                    locality = item.getString("long_name");
                                    if (!TextUtils.isEmpty(locality)) {
                                        localityName = locality;
                                        return localityName;
                                    }
                                    break;
                                } else if ("administrative_area_level_2".equals(types.get(j))) {
                                    administrative_area_level_2 = item.getString("long_name");
                                    break;
                                } else if ("administrative_area_level_1".equals(types.get(j))) {
                                    administrative_area_level_1 = item.getString("long_name");
                                    break;
                                }
                            }
                        }
                    }
                }

                if (!TextUtils.isEmpty(locality)) {
                    localityName = locality;
                } else if (!TextUtils.isEmpty(administrative_area_level_2)) {
                    localityName = administrative_area_level_2;
                } else if (!TextUtils.isEmpty(administrative_area_level_1)) {
                    localityName = administrative_area_level_1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return localityName;
    }

    @SuppressLint("MissingPermission")
    private Location getLastLocation(LocationManager lm) {
        try {
            Location lastNet = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location lastGPS = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastNet == null && lastGPS == null) {
                return null;
            } else if (lastNet != null && lastGPS != null) {
                return lastNet.getTime() > lastGPS.getTime() ? lastNet : lastGPS;
            } else {
                return lastNet != null ? lastNet : lastGPS;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            // ignore (if provider(s) is unknown)
        }
        return null;
    }


    public static String getDistanceText(double lat1, double lon1, double lat2, double lon2) {
        double distance = com.agmbat.android.utils.LocationHelper.getDistance(lat1, lon1, lat2, lon2);
        String distanceText = "";
        if (distance > 1609000) {
            distanceText = ">1000mi";
        } else if (distance > 304.8) {
            distanceText = String.format("%.2fmi", distance / 1609.0);
        } else {
            distanceText = String.format("%.0fft", distance / 0.3048);
        }
        return distanceText;
    }

}
