package org.jivesoftware.smackx.block;

public interface BlockListener {

    public void notifyFetchBlockListNameResult(boolean success);
    public void notifyFetchBlockResult(boolean success);

    public void notifyAddBlockResult(String jid, boolean success);

    public void notifyRemoveBlockResult(String jid, boolean success);

    public void notifyBlockListChange(String jid, boolean isBlock);
}
