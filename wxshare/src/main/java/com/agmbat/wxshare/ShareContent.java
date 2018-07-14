package com.agmbat.wxshare;

import java.io.File;
import java.util.List;

/**
 * 分享的内容
 */
public class ShareContent {

    /**
     * 分享的内容
     */
    public String mDescription;

    public List<File> mImageFileList;

    /**
     * 分享给朋友, 是分享文本还是图片
     */
    public boolean mShareFriendText = true;
}
