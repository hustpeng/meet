package org.jivesoftware.smackx.findserver;


/**
 * A listener that is fired any time a favorites is changed
 */
public interface FindServerListener {
    /**
     * @param serverObject
     */
    public void notifyFindServersResult(FindServerObject serverObject);
}