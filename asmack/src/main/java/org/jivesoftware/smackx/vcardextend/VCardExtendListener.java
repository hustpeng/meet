package org.jivesoftware.smackx.vcardextend;

/**
 * A listener that is fired any time a favorites is changed
 */
public interface VCardExtendListener {

    /**
     * @param jid
     * @param vcardExtend 取到的vcardExtend信息或者null
     */
    public void notifyFetchVCardExtendResult(String jid, VCardExtendObject vcardExtend);

    /**
     * @param success
     */
    public void notifySetMyVCardExtendResult(boolean success);
}