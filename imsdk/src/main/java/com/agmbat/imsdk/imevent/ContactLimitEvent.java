package com.agmbat.imsdk.imevent;

public class ContactLimitEvent {

    private int mLimit;

    public ContactLimitEvent(int limit) {
        mLimit = limit;
    }

    public int getLimit() {
        return mLimit;
    }
}
