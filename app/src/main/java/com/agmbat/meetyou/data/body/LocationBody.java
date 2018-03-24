package com.agmbat.meetyou.data.body;

import android.location.Location;
import android.os.Bundle;

public class LocationBody extends Body {

    private final Location mLocation;

    public LocationBody(Location location) {
        mLocation = location;
    }

    public Location getLocation() {
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
        Bundle extra = mLocation.getExtras();
        if (extra != null) {
            // TODO
            address = extra.getString("EXTRA_ADDRESS");
        }
        builder.append("<address>").append(address).append("</address>");
        builder.append("</wrap>");
        return builder.toString();
    }

}

