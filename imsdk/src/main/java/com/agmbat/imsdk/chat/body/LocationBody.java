package com.agmbat.imsdk.chat.body;

import com.baidu.location.BDLocation;

public class LocationBody extends Body {


    public static final String EXTRA_ADDRESS = "address";

    private final BDLocation mLocation;

    public LocationBody(BDLocation location) {
        mLocation = location;
    }

    public BDLocation getLocation() {
        return mLocation;
    }

    @Override
    public BodyType getBodyType() {
        return BodyType.GEOLOC;
    }

    @Override
    public String toXml() {
        StringBuilder builder = new StringBuilder();
        builder.append("<wrap>");
        builder.append("<type>").append(getBodyType()).append("</type>");
        builder.append("<lat>").append(mLocation.getLatitude()).append("</lat>");
        builder.append("<lon>").append(mLocation.getLongitude()).append("</lon>");
        String address = "";
//        Bundle extra = mLocation.getExtras();
//        if (extra != null) {
//            address = extra.getString(EXTRA_ADDRESS);
//        }
        builder.append("<address>").append(address).append("</address>");
        builder.append("</wrap>");
        return builder.toString();
    }

}

