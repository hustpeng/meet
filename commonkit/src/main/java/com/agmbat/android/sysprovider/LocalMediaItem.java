package com.agmbat.android.sysprovider;

//
// LocalMediaItem is an abstract class captures those common fields
// in LocalImage and LocalVideo.
//
public abstract class LocalMediaItem {

    public static final double INVALID_LATLNG = 0f;

    // database fields
    public int id;
    public String caption;
    public String mimeType;
    public long fileSize;
    public double latitude = INVALID_LATLNG;
    public double longitude = INVALID_LATLNG;
    public long dateTakenInMs;
    public long dateAddedInSec;
    public long dateModifiedInSec;
    public String filePath;
    public int bucketId;
    public int width;
    public int height;

    public LocalMediaItem() {
    }

    public long getDateInMs() {
        return dateTakenInMs;
    }

    public String getName() {
        return caption;
    }

    public void getLatLong(double[] latLong) {
        latLong[0] = latitude;
        latLong[1] = longitude;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getBucketId() {
        return bucketId;
    }

    public String getMimeType() {
        return mimeType;
    }

    public long getSize() {
        return fileSize;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
