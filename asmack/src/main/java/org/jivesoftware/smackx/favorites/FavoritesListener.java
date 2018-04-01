
package org.jivesoftware.smackx.favorites;


/**
 * A listener that is fired any time a favorites is changed
 *
 */
public interface FavoritesListener {

    public void favoritesChanged();

    public void notifyFetchFavoritesResult(boolean success);

    public void notifyAddFavoritesResult(String jid, boolean success);

    public void notifyRemoveFavoritesResult(String jid, boolean success);
}