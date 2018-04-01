
package org.jivesoftware.smackx.favoritedme;

public interface FavoritedMeListener {
    public void notifyFavoritedMeChanged();

    public void notifyFetchFavoritedMeResult(boolean success, String error);
    public void notifyNewFavoritedMe(int count);
}