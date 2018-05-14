package org.jivesoftware.smackx.message;

public interface MessageListener {
    /**
     * @param messageObject
     * @return
     */
    public boolean willInsertReceivedMsg(MessageObject messageObject);

    public void didReceiveNewMsg(MessageObject messageObject);

    /**
     * @param messageObject
     */
    public void willInsertUnReadyMsg(MessageObject messageObject);

    /**
     * @param messageObject
     */
    public void updateCompletedImageMsg(MessageObject messageObject);

    /**
     * @param messageObject
     */
    public void updateCompletedLocationMsg(MessageObject messageObject);
}