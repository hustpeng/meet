package com.agmbat.meetyou.data.body;

import com.agmbat.android.sysprovider.LocalImage;

public class FireBody extends ImageBody {

    public FireBody(String fileUrl, LocalImage image) {
        super(fileUrl, image);
    }

    public FireBody(String fileUrl, Image image) {
        super(fileUrl, image);
    }

    @Override
    public BodyType getBodyType() {
        return BodyType.FIRE;
    }
}