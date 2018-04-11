package org.jivesoftware.smackx.vcard;

/**
 * A listener that is fired any time a favorites is changed
 */
public interface VCardListener {

    /**
     * @param jid
     * @param vcard 取到的VCard信息或者null
     */
    public void notifyFetchVCardResult(String jid, VCardObject vcard);

    /**
     * @param success
     */
    public void notifySetMyVCardResult(boolean success);
}