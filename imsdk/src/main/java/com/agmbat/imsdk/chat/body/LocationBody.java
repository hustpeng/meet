package com.agmbat.imsdk.chat.body;

import com.agmbat.map.LocationObject;

public class LocationBody extends Body {

    private final LocationObject mLocation;

    public LocationBody(LocationObject location) {
        mLocation = location;
    }

    public LocationObject getLocation() {
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
        builder.append("<lat>").append(mLocation.mLatitude).append("</lat>");
        builder.append("<lon>").append(mLocation.mLongitude).append("</lon>");
        builder.append("<address>").append(mLocation.mAddress).append("</address>");
        builder.append("</wrap>");
        return builder.toString();
    }

}

