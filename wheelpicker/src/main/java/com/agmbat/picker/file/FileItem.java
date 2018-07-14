package com.agmbat.picker.file;

import android.graphics.drawable.Drawable;


import com.agmbat.picker.util.ConvertUtils;

import java.io.Serializable;

/**
 * 文件项信息
 */
public class FileItem implements Serializable {

    private Drawable icon;
    private String name;
    private String path = "/";
    private long size = 0;
    private boolean isDirectory = false;

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean isDirectory) {
        this.isDirectory = isDirectory;
    }

    @Override
    public String toString() {
        return ConvertUtils.toString(this);
    }
}
