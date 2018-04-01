package org.jivesoftware.smackx.permit;

public interface PermitListener {
    public void notifyFetchPermitResult(boolean success);

    public void notifyAddPermitResult(String jid, boolean success);

    public void notifyRemovePermitResult(String jid, boolean success);

    public void notifyPermitListChange(String jid, boolean isPermit);
}
