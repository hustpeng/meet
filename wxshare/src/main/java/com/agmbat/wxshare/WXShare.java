package com.agmbat.wxshare;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import com.agmbat.android.utils.AFileUtils;
import com.agmbat.android.utils.ApkUtils;
import com.agmbat.menu.GridMenu;
import com.agmbat.menu.MenuInfo;
import com.agmbat.menu.OnClickMenuListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 微信分享
 * 分享给微信好友：只能分享[纯图片] 或 [纯文本]
 * 朋友圈：可以文字和图片同时分享
 */
public class WXShare {

    /**
     * 微信包名
     */
    private static final String PACKAGE_NAME = "com.tencent.mm";

    /**
     * 分享文字的Activity, 图片等到朋友
     */
    private static final String SHARE_IMAGE = "com.tencent.mm.ui.tools.ShareImgUI";

    /**
     * 分享到朋友圈的Activity
     */
    private static final String SHARE_TO_TIME_LINE = "com.tencent.mm.ui.tools.ShareToTimeLineUI";

    /**
     * 分享key描述
     */
    private static final String KEY_DESCRIPTION = "Kdescription";


    /**
     * 分享[视频]到微信朋友或群
     *
     * @param context
     * @param file
     */
    public static void shareVideoToFriend(Context context, File file) {
        if (!ApkUtils.isInstalled(PACKAGE_NAME)) {
            Toast.makeText(context, "您还没有安装微信！", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setClassName(PACKAGE_NAME, SHARE_IMAGE);
        intent.setType("video/*");
        //intent.setFlags(0x3000001);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        context.startActivity(intent);
    }

    /**
     * 分享[文件]给朋友
     *
     * @param context
     * @param file
     */
    public static void shareFileToFriend(Context context, File file) {
        if (!ApkUtils.isInstalled(PACKAGE_NAME)) {
            Toast.makeText(context, "您还没有安装微信！", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setClassName(PACKAGE_NAME, SHARE_IMAGE);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        // 此处可发送多种文件
        intent.setType(AFileUtils.getMimeType(file.getAbsolutePath()));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }

    /**
     * 分享[文本]到微信朋友或群
     *
     * @param context
     * @param text
     */
    public static void shareTextToFriend(Context context, String text) {
        if (!ApkUtils.isInstalled(PACKAGE_NAME)) {
            Toast.makeText(context, "您还没有安装微信！", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setClassName(PACKAGE_NAME, SHARE_IMAGE);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setType("text/*");
        context.startActivity(intent);
    }

    /**
     * 分享[url]到微信朋友或群
     * <p>
     * 测试发现 url 分享不成功
     *
     * @param context
     * @param text
     * @param url
     */
    public static void shareUrlToFriend(Context context, String text, String url) {
        if (!ApkUtils.isInstalled(PACKAGE_NAME)) {
            Toast.makeText(context, "您还没有安装微信！", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setClassName(PACKAGE_NAME, SHARE_IMAGE);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.putExtra(Intent.EXTRA_STREAM, url);
        intent.setType("text/*");
        context.startActivity(intent);
    }

    /**
     * 分享[图片]给微信朋友或群
     *
     * @param context
     * @param description
     * @param bitmap
     */
    public static void shareImageToFriend(Context context, String description, Bitmap bitmap) {
        String url = saveBitmapToSystemMedia(context, bitmap);
        Uri uri = Uri.parse(url);
        shareImageToFriend(context, description, uri);
    }

    /**
     * 分享[图片]给微信朋友或群
     *
     * @param context
     * @param description
     * @param file        图片文件
     */
    public static void shareImageToFriend(Context context, String description, File file) {
        Uri uri = Uri.fromFile(file);
        shareImageToFriend(context, description, uri);
    }

    /**
     * 分享[图片]给微信朋友或群
     *
     * @param context
     * @param description
     * @param uri
     */
    public static void shareImageToFriend(Context context, String description, Uri uri) {
        if (!ApkUtils.isInstalled(PACKAGE_NAME)) {
            Toast.makeText(context, "您还没有安装微信！", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setClassName(PACKAGE_NAME, SHARE_IMAGE);
        intent.setType("image/*");
        intent.putExtra(KEY_DESCRIPTION, description);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(intent);
    }

    /**
     * 分享[多张图片]给微信朋友或群
     *
     * @param context
     * @param imageUriList
     */
    public static void shareMultipleImageToFriend(Context context, ArrayList<Uri> imageUriList) {
        if (!ApkUtils.isInstalled(PACKAGE_NAME)) {
            Toast.makeText(context, "您还没有安装微信！", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.setClassName(PACKAGE_NAME, SHARE_IMAGE);
        intent.setType("image/*");
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUriList);
        context.startActivity(intent);
    }

    /**
     * 分享[图片]到微信朋友圈
     */
    public static void shareImageToTimeLine(Context context, String msg, Bitmap bitmap) {
        if (!ApkUtils.isInstalled(PACKAGE_NAME)) {
            Toast.makeText(context, "您还没有安装微信！", Toast.LENGTH_SHORT).show();
            return;
        }
        String url = saveBitmapToSystemMedia(context, bitmap);
        Uri uriToImage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setClassName(PACKAGE_NAME, SHARE_TO_TIME_LINE);
        intent.setType("image/*");
        intent.putExtra(KEY_DESCRIPTION, msg);
        //intent.putExtra(Intent.EXTRA_TEXT, "推荐一个很酷的应用");
        //intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        intent.putExtra(Intent.EXTRA_STREAM, uriToImage);
        context.startActivity(intent);
    }

    /**
     * 分享[多张图片]到朋友圈
     *
     * @param context
     * @param description
     * @param imageUriList
     */
    public static void shareMultipleImageToTimeLine(Context context, String description, ArrayList<Uri> imageUriList) {
        if (!ApkUtils.isInstalled(PACKAGE_NAME)) {
            Toast.makeText(context, "您还没有安装微信！", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.setClassName(PACKAGE_NAME, SHARE_TO_TIME_LINE);
        intent.setType("image/*");
        intent.putExtra(KEY_DESCRIPTION, description);
        // //图片数据，支持本地uri
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUriList);
        // 下面方式也可以
        // intent.putExtra(Intent.EXTRA_STREAM, imageUriList);
        context.startActivity(intent);
    }

    /**
     * 分享多张图片到朋友圈
     *
     * @param context
     * @param description
     * @param paths
     */
    public static void shareMultipleImagePathToTimeLine(Context context, String description, List<File> fileList) {
        ArrayList<Uri> imageUriList = new ArrayList<>();
        for (File file : fileList) {
            if (file.exists()) {
                imageUriList.add(Uri.fromFile(file));
            }
        }
        if (imageUriList.size() == 0) {
            Toast.makeText(context, "图片不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        shareMultipleImageToTimeLine(context, description, imageUriList);
    }

    private static String saveBitmapToSystemMedia(Context context, Bitmap bitmap) {
        return MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, null, null);
    }


    /**
     * 显示分享对话框
     *
     * @param context
     */
    public static void share(final Context context, final ShareContent content) {
        GridMenu dialog = new GridMenu(context);
        MenuInfo menuInfo = new MenuInfo();
        menuInfo.setIcon(R.drawable.wxshare_friend);
        menuInfo.setTitle(context.getString(R.string.wxshare_friend));
        menuInfo.setOnClickMenuListener(new OnClickMenuListener() {
            @Override
            public void onClick(MenuInfo menu, int index) {
                if (content.mShareFriendText) {
                    shareTextToFriend(context, content.mDescription);
                } else {
                    shareImageToFriend(context, content.mDescription, content.mImageFileList.get(0));
                }
            }
        });
        dialog.addItem(menuInfo);

        menuInfo = new MenuInfo();
        menuInfo.setIcon(R.drawable.wxshare_time_line);
        menuInfo.setTitle(context.getString(R.string.wxshare_time_line));
        menuInfo.setOnClickMenuListener(new OnClickMenuListener() {
            @Override
            public void onClick(MenuInfo menu, int index) {
                shareMultipleImagePathToTimeLine(context, content.mDescription, content.mImageFileList);
            }
        });
        dialog.addItem(menuInfo);
        dialog.show();
    }


}
