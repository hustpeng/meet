package com.agmbat.meetyou;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import com.agmbat.android.utils.ApkUtils;

import java.util.ArrayList;

public class Share {

    private static final String PACKAGE_NAME = "com.tencent.mm";
    private static final String SHARE_IMAGE = "com.tencent.mm.ui.tools.ShareImgUI";
    private static final String SHARE_TO_TIME_LINE = "com.tencent.mm.ui.tools.SHARE_TO_TIME_LINE";
    private static final String KEY_DESCRIPTION = "Kdescription";

    /**
     * 分享到微信朋友圈
     */
    private void shareToWechatFriendCircle(Context context) {
        Bitmap bitmap = loadBitmap();
        String url = saveBitmapToSystemMedia(context, bitmap);
        Uri uriToImage = Uri.parse(url);
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setClassName(PACKAGE_NAME, SHARE_TO_TIME_LINE);
            intent.setType("image/*");
            //File file = new File(imgPath);
            String msg = "AppResources.getText(R.string.share_app_recommended).toString() + AppResources.getString(R.string.app_name)";
            //intent.putExtra("Kdescription", ResourcesManager.getText(R.string.share_app_recommended).toString() + ResourcesManager.getString(R.string.app_name));
            intent.putExtra(KEY_DESCRIPTION, msg);
            //intent.putExtra(Intent.EXTRA_TEXT, "推荐一个很酷的应用");
            //intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.putExtra(Intent.EXTRA_STREAM, uriToImage);
            context.startActivity(intent);
        } catch (Exception e) {
        }
    }


    public static void shareImgs(Activity activity, ArrayList<Uri> images) {
        if (ApkUtils.isInstalled(PACKAGE_NAME)) {
            Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            intent.setType("image/*");
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, images);
            intent.setClassName(PACKAGE_NAME, SHARE_IMAGE);
            activity.startActivity(intent);
        } else {
            Toast.makeText(activity, "您还没有安装微信！", Toast.LENGTH_SHORT).show();
        }
    }

    public static void shareText(Activity activity, String msg) {
        if (ApkUtils.isInstalled(PACKAGE_NAME)) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, msg);
            intent.setType("text/*");
            intent.setClassName(PACKAGE_NAME, SHARE_IMAGE);
            activity.startActivity(intent);
        } else {
            Toast.makeText(activity, "您还没有安装微信！", Toast.LENGTH_SHORT).show();
        }
    }

    public static void share(Activity activity, String msg, ArrayList<Uri> images) {
        if (ApkUtils.isInstalled(PACKAGE_NAME)) {
            Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            intent.putExtra(KEY_DESCRIPTION, msg);
            intent.setType("image/*");
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, images);
            intent.setClassName(PACKAGE_NAME, SHARE_TO_TIME_LINE);
            activity.startActivity(intent);
        } else {
            Toast.makeText(activity, "您还没有安装微信！", Toast.LENGTH_SHORT).show();
        }
    }


    /*
    * 分享给微信朋友或群
    *
    * */
    public void shareToWechat(Context context) {
        Bitmap bitmap = loadBitmap();
        String url = saveBitmapToSystemMedia(context, bitmap);
        Uri uriToImage = Uri.parse(url);
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setClassName(PACKAGE_NAME, SHARE_IMAGE);
            intent.setType("image/*");
            intent.putExtra(KEY_DESCRIPTION, "ResourcesManager.getText(R.string.share_app_recommended).toString()");
            intent.putExtra(Intent.EXTRA_STREAM, uriToImage);
            context.startActivity(intent);
        } catch (Exception e) {

        }
    }

    private void shareTo(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setIcon(R.drawable.icon_share);
        builder.setTitle("ResourcesManager.getText(R.string.share_shareto_title)");
        //    指定下拉列表的显示数据
        String wch = "ResourcesManager.getText(R.string.share_app_towechat).toString()";
        String wchc = "ResourcesManager.getText(R.string.share_app_towechat_circle).toString()";

        final String[] apps = {wch, wchc};
        //    设置一个下拉的列表选择项
        builder.setItems(apps, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        shareToWechat(context);
                        break;
                    case 1:
                        shareToWechatFriendCircle(context);
                        break;
                }
            }
        });

        AlertDialog dialog = builder.create();
        //dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }

    private static Bitmap loadBitmap() {
        //InputStream is = getClass().getResourceAsStream("/assets/qrcode.png");
//        InputStream is = getClass().getResourceAsStream(Constants.SHARE_ME_IMAGES_PATH);
        Bitmap bitmap = BitmapFactory.decodeStream(null);
        return bitmap;
    }

    private static String saveBitmapToSystemMedia(Context context, Bitmap bitmap) {
        return MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, null, null);
    }


}
