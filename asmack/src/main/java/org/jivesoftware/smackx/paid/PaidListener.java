package org.jivesoftware.smackx.paid;

public interface PaidListener {
    public void notifyFetchAccountResult(PaidAccountObject paidObject);

    public void notifyFetchPageInfoResult(PaidPageInfoObject paidObject);
}