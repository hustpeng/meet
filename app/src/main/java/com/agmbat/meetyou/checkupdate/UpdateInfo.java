package com.agmbat.meetyou.checkupdate;

import com.google.gson.annotations.SerializedName;

public class UpdateInfo {

    @SerializedName("app_version")
    public int app_version;

    @SerializedName("title")
    public String title;

    @SerializedName("changelog")
    public String changelog;

    @SerializedName("url")
    public String url;

    @SerializedName("url_backup")
    public String url_backup;

    @SerializedName("can_skip")
    public boolean can_skip;
}
