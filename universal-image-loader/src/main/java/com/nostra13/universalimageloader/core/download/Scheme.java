package com.nostra13.universalimageloader.core.download;

public class Scheme {

    public static String getScheme(String imageUri) {
        return imageUri.substring(0, imageUri.indexOf(":"));
    }

    /**
     * Removed scheme part ("scheme://") from incoming URI
     */
    public static String crop(String uri, String scheme) {
        String uriPrefix = scheme + "://";
        return uri.substring(uriPrefix.length());
    }

    /**
     * Appends scheme to incoming path
     */
    public static String wrapUri(String scheme, String path) {
        return scheme + "://" + path;
    }

//    private boolean belongsTo(String uri) {
//        return uri.toLowerCase(Locale.US).startsWith(uriPrefix);
//    }
//

}
