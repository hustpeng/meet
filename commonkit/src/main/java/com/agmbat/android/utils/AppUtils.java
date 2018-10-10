/**
 * Copyright (C) 2016 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 * <p>
 * Android Common Kit
 *
 * @author mayimchen
 * @since 2016-07-23
 */
package com.agmbat.android.utils;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.IntentSender;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;

import com.agmbat.android.AppResources;
import com.agmbat.android.SystemManager;
import com.agmbat.file.FileUtils;
import com.agmbat.io.IoUtils;
import com.agmbat.log.Log;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * 提供action操作，打开系统工具
 */
public class AppUtils {

    private static final String TAG = "AppUtils";

    // sm
    private static final String SMS_BODY_KEY = "sms_body";
    private static final String KEY_TREAT_UP_AS_BACK = "treat-up-as-back";

    // short cut
    private static final String SHORTCUT_INSTALL = "com.android.launcher.action.INSTALL_SHORTCUT";
    private static final String SHORTCUT_UNINSTALL = "com.android.launcher.action.UNINSTALL_SHORTCUT";
    private static final String SHORTCUT_KEY_DUPLICATE = "duplicate";
    private static final String SHORTCUT_CONTENT_URI1 =
            "content://com.android.launcher2.settings/favorites?notify=true";
    private static final String SHORTCUT_CONTENT_URI2 = "content://com.android.launcher.settings/favorites?notify=true";

    private static final String TYPE_TEXT_PLAIN = "text/plain";

    /**
     * 是否为debug模式
     *
     * @return
     */
    public static boolean debuggable() {
        return debuggable(SystemManager.getContext());
    }

    /**
     * 是否为debug模式, 使用BuildConfig文件中DEBUG变量会不太稳定
     *
     * @param context
     * @return
     */
    public static boolean debuggable(Context context) {
        ApplicationInfo info = context.getApplicationInfo();
        return ((info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0);
    }

    /**
     * 杀死当前进程
     */
    public static void killMyProcess() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public static void restartPackage() {
        restartPackage(AppResources.getAppContext().getPackageName());
    }

    public static void restartPackage(String packageName) {
        SystemManager.getActivityManager().restartPackage(packageName);
    }

    /**
     * @param context
     * @param pendingIntent
     * @param fillInIntent
     * @return
     */
    public static boolean startPendingIntentSafely(Context context, PendingIntent pendingIntent, Intent fillInIntent) {
        if (null == pendingIntent) {
            return false;
        }
        try {
            // TODO: Unregister this handler if PendingIntent.FLAG_ONE_SHOT?
            context.startIntentSender(pendingIntent.getIntentSender(), fillInIntent, Intent.FLAG_ACTIVITY_NEW_TASK,
                    Intent.FLAG_ACTIVITY_NEW_TASK, 0);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Cannot send pending intent: ", e);
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Cannot send pending intent due to " + "unknown exception: ", e);
            return false;
        }
        return true;
    }

    public static boolean startActivity(Context context, Intent intent) {
        try {
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException ex) {
            ToastUtil.showToast("无法找到相应的程序");
            Log.e(TAG, "Failed to start activity", ex);
        }
        return false;
    }

    public static boolean startActivityForResult(Activity activity, Intent intent, int requestCode) {
        try {
            activity.startActivityForResult(intent, requestCode);
            return true;
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "Failed to start activity", e);
        }
        return false;
    }

    /**
     * 模拟按下home键,实现切换到后台
     *
     * @param context
     * @return
     */
    public static boolean launchHome(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        return startActivity(context, intent);
    }

    /**
     * 启动指定的应用
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean launchPackage(Context context, String packageName) {
        if (null == context || TextUtils.isEmpty(packageName)) {
            return false;
        }
        Intent intent = SystemManager.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            return startActivity(context, intent);
        }
        return false;
    }

    /**
     * 播放视频
     *
     * @param context
     * @param path
     * @param title
     * @return
     */
    public static boolean playVideo(Context context, String path, String title) {
        Uri uri = parseUri(path);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "video/*");
        intent.putExtra(Intent.EXTRA_TITLE, title);
        intent.putExtra(KEY_TREAT_UP_AS_BACK, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return startActivity(context, intent);
    }

    /**
     * 播放视频
     *
     * @param activity
     * @param path
     * @param title
     * @param requestCode
     * @return
     */
    public static boolean playVideo(Activity activity, String path, String title, int requestCode) {
        Uri uri = parseUri(path);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "video/*");
        intent.putExtra(Intent.EXTRA_TITLE, title);
        intent.putExtra(KEY_TREAT_UP_AS_BACK, true);
        return startActivityForResult(activity, intent, requestCode);
    }

    public static boolean viewImage(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "image/*");
        return startActivity(context, intent);
    }

    /**
     * 打开照相机拍照
     */
    @Deprecated
    public static boolean openCamera(Activity activity, String path, int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(path)));
        return startActivityForResult(activity, intent, requestCode);
    }


    /**
     * 打开照相机拍照
     */
    @Deprecated
    public static void openCamera(Fragment fragment, String path, int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(path)));
        fragment.startActivityForResult(intent, requestCode);
    }


    /**
     * 打开相册，准备挑选图片, 高版本使用会crash
     *
     * @param activity
     * @param requestCode
     */
    @Deprecated
    public static boolean openGallery(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        return startActivityForResult(activity, intent, requestCode);
    }

    /**
     * 打开相册，准备挑选图片, 高版本使用会crash
     */
    @Deprecated
    public static void openGallery(Fragment fragment, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * 照相后或者从相册挑选图片后，进行裁剪
     *
     * @param uri
     */
    public static boolean openCrop(Activity activity, Uri uri, int requestCode) {
        return openCrop(activity, uri, 150, requestCode);
    }

    /**
     * 照相后或者从相册挑选图片后，进行裁剪
     *
     * @param uri
     */
    public static boolean openCrop(Activity activity, Uri uri, int size, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", true);
        return startActivityForResult(activity, intent, requestCode);
    }

    /**
     * 照相后或者从相册挑选图片后，进行裁剪
     *
     * @param uri
     */
    public static void openCrop(Fragment fragment, Uri uri, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);// 裁剪大小
        intent.putExtra("return-data", true);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static boolean openAvatarCrop(Activity activity, Uri uri, Uri dst, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 160);
        intent.putExtra("outputY", 160);
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, dst);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        return startActivityForResult(activity, intent, requestCode);
    }

    /**
     * 获取联系人信息
     *
     * @param activity
     * @param requestCode
     */
    public static boolean pickContact(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        return startActivityForResult(activity, intent, requestCode);
    }

    /**
     * 获取一个手机号码
     *
     * @param activity
     * @param requestCode
     */
    public static boolean pickPhone(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        return startActivityForResult(activity, intent, requestCode);
    }

    /**
     * 打开文件
     */
    public static boolean openFile(Context context, File file) {
        String extension = FileUtils.getExtension(file);
        String mimetype = AFileUtils.getMimeTypeFromExtension(extension);
        if (mimetype == null) {
            Log.e(TAG, "can not get mime type, file extension:" + extension);
            mimetype = "*/*";
        }
        Log.d(TAG, file.getName() + " file type is " + mimetype);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, mimetype);
        return startActivity(context, intent);
    }

    /**
     * Open the file
     */
    public static boolean openFile(Context context, String filename, String mimetype) {
        Uri uri = Uri.parse(filename);
        // If there is no scheme, then it must be a file
        if (uri.getScheme() == null) {
            uri = Uri.fromFile(new File(filename));
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, mimetype);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return startActivity(context, intent);
    }

    /**
     * 通过浏览器打开url
     *
     * @param context
     * @param url
     */
    public static boolean openBrowser(Context context, String url) {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        // intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return startActivity(context, intent);
    }

    /**
     * 打开market查看应用
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean openMarket(Context context, String packageName) {
        Uri marketUri = Uri.parse("market://details?id=" + packageName);
        Intent intent = new Intent(Intent.ACTION_VIEW, marketUri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        return startActivity(context, intent);
    }

    /**
     * 打开系统Service界面
     *
     * @param context
     * @return
     */
    public static boolean openRunningServices(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.RunningServices");
        return startActivity(context, intent);
    }

    /**
     * 调用系统打电话界面,添加uses-permission,android:name="android.permission.CALL_PHONE"
     *
     * @param context
     * @param phoneNumber
     * @return
     */
    public static boolean callPhone(Context context, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        return startActivity(context, intent);
    }

    /**
     * 发送短信
     *
     * @param context
     * @param phone
     * @param smsBody
     * @return
     */
    public static boolean sendSms(Context context, String phone, String smsBody) {
        Uri uri = Uri.parse("smsto:" + phone);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra(SMS_BODY_KEY, smsBody);
        return startActivity(context, intent);
    }

    /**
     * Send email
     */
    public static boolean sendEmail(Context context, String[] emails, String subject, String body) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, emails);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.setType(TYPE_TEXT_PLAIN);
        return startActivity(context, intent);
    }

    /**
     * 调用系统的分享功能
     *
     * @param context
     * @param subject
     * @param body
     */
    public static void shareChooseClient(Context context, String subject, String body) {
        Intent intent = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
        intent.setType(TYPE_TEXT_PLAIN);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.putExtra(Intent.EXTRA_CC, new String[]{""});
        context.startActivity(Intent.createChooser(intent, "Choose Email Client"));
    }

    /**
     * 调用系统share
     *
     * @param context
     * @return
     */
    public static boolean share(Context context, String subject, String body) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT, body);
        i.setType(TYPE_TEXT_PLAIN);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return startActivity(context, i);
    }

    public static void share(Context context, String subject, String text, File file) {
        share(context, subject, text, file, null);
    }

    public static void share(Context context, String subject, String text, File file, String mimeType) {
        if (TextUtils.isEmpty(mimeType)) {
            String extension = FileUtils.getExtension(file);
            mimeType = AFileUtils.getMimeTypeFromExtension(extension);
        }
        final Uri uri = Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(mimeType);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(intent);
    }

    /**
     * 分享文件
     *
     * @param context
     * @param file
     * @return
     */
    public static boolean shareAttachment(Context context, File file) {
        if (file == null) {
            return false;
        }
        String extension = FileUtils.getExtension(file);
        String mimeType = AFileUtils.getMimeTypeFromExtension(extension);
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        final Uri pathUri = Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(mimeType);
        intent.putExtra(Intent.EXTRA_STREAM, pathUri);
        return startActivity(context, Intent.createChooser(intent, "Choose Share Client"));
    }

    /**
     * 为url创建快捷方式
     *
     * @param context
     * @param url
     * @param shortcutName
     * @param shortcutIconRes
     */
    public static void createShortcut(Context context, String url, String shortcutName, int shortcutIconRes) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        createShortcut(context, intent, shortcutName, shortcutIconRes);
    }

    /**
     * 为程序创建桌面快捷方式
     * <p>
     * 需要权限"com.android.launcher.permission.INSTALL_SHORTCUT"
     * </p>
     */
    public static void createShortcut(Context context, Intent intent, String shortcutName, int shortcutIconRes) {
        if (hasShortcut(context, shortcutName)) {
            return;
        }
        intent.putExtra(Intent.EXTRA_TEXT, "short_cut");
        Intent shortcut = new Intent(SHORTCUT_INSTALL);
        ShortcutIconResource icon = Intent.ShortcutIconResource.fromContext(context, shortcutIconRes); // 获取快捷键的图标
        shortcut.putExtra(SHORTCUT_KEY_DUPLICATE, false);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutName);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        context.sendBroadcast(shortcut);
    }

    /**
     * 创建快捷方式
     *
     * @param context
     * @param intent
     * @param shortcutName
     * @param shortcutBitmap
     */
    public static void createShortcut(Context context, Intent intent, String shortcutName, Bitmap shortcutBitmap) {
        Intent shortcut = new Intent(SHORTCUT_INSTALL);
        // Do not allow duplicate items
        shortcut.putExtra(SHORTCUT_KEY_DUPLICATE, false);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutName);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON, shortcutBitmap);
        context.sendBroadcast(shortcut);
    }

    /**
     * 判断桌面上的快捷方式是否存在
     *
     * @param context
     * @param shortcurtName
     * @return
     */
    public static boolean hasShortcut(Context context, String shortcurtName) {
        boolean isInstalled = false;
        String url = Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO ? SHORTCUT_CONTENT_URI2 : SHORTCUT_CONTENT_URI1;
        Cursor c =
                context.getContentResolver().query(Uri.parse(url), new String[]{"title", "iconResource"}, "title=?",
                        new String[]{shortcurtName}, null);
        if (c != null) {
            isInstalled = c.getCount() > 0;
            c.close();
        }
        return isInstalled;
    }

    /**
     * 删除程序的快捷方式
     */
    public static void deleteShortcut(Context context, Intent intent, String shortcurtName) {
        Intent shortcut = new Intent(SHORTCUT_UNINSTALL);
        // 快捷方式的名称
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcurtName);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        context.sendBroadcast(shortcut);
    }

    /**
     * 振动1秒
     */
    public static void vibrator() {
        SystemManager.getVibrator().vibrate(1000);
    }

    /**
     * 获取前台运行的Activity
     *
     * @return
     */
    public static ComponentName getTopActivity() {
        ActivityManager am = SystemManager.getActivityManager();
        List<RunningTaskInfo> rti = am.getRunningTasks(1);
        return rti.get(0).topActivity;
    }

    /**
     * 判断指定应用是否在运行
     *
     * @param packageName
     * @return
     */
    public static boolean isAppRunning(String packageName) {
        if (!TextUtils.isEmpty(packageName)) {
            ActivityManager am = SystemManager.getActivityManager();
            List<RunningAppProcessInfo> runningAppProcessInfos = am.getRunningAppProcesses();
            for (RunningAppProcessInfo processInfo : runningAppProcessInfos) {
                if (packageName.equals(processInfo.processName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if is service defined by the "serviceClassName" is running?
     *
     * @param serviceClassName Service class name to check
     * @return true, if is service running
     */
    public static boolean isServiceRunning(String serviceClassName) {
        ActivityManager am = SystemManager.getActivityManager();
        List<RunningServiceInfo> services = am.getRunningServices(Integer.MAX_VALUE);
        for (RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(serviceClassName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断当前界面是否是桌面
     */
    public static boolean isTopAppHome() {
        return ApkUtils.getHomePackages().contains(getTopActivity().getPackageName());
    }

    /**
     * 判断当前app是否在前台运行
     *
     * @return
     */
    public static boolean isRunningForeground() {
        String foreground = getTopActivity().getPackageName();
        String current = ApkUtils.getPackageName();
        return current.equals(foreground);
    }

    /**
     * 获取当前进程名称
     *
     * @return
     */
    public static String getProcessName() {
        int pid = android.os.Process.myPid();
        ActivityManager am = SystemManager.getActivityManager();
        for (ActivityManager.RunningAppProcessInfo process : am.getRunningAppProcesses()) {
            if (process.pid == pid) {
                return process.processName;
            }
        }
        return null;
    }

    /**
     * 通过ps命令获取当前进程名称,此方法可能会不太准确
     *
     * @return
     */
    public static String getProcessName2() {
        int pid = android.os.Process.myPid();
        InputStream is = null;
        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("ps");
            is = process.getInputStream();
            String result = IoUtils.loadContent(is);
            if (!TextUtils.isEmpty(result)) {
                String[] lines = result.split("\n");
                for (int i = 1; i < lines.length; i++) {
                    String line = lines[i];
                    String[] array = line.split("\\s{1,}");
                    int processPid = Integer.parseInt(array[1]);
                    if (pid == processPid) {
                        return array[8];
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(is);
        }
        return null;
    }

    public static void setClipboardText(Context context, CharSequence text) {
        android.content.ClipboardManager cm = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(text);
    }

    /**
     * 获取剪切板内容
     *
     * @param context
     * @return
     */
    public static String getClipboardText(Context context) {
        CharSequence text = null;
        if (android.os.Build.VERSION.SDK_INT < 11) {
            android.text.ClipboardManager cbm =
                    (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            text = cbm.getText();
        } else {
            android.content.ClipboardManager cbm =
                    (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            text = cbm.getText();
        }
        return text == null ? "" : text.toString();
    }

    // From http://developer.android.com/training/implementing-navigation/ancestral.html#NavigateUp .
    public static void navigateUp(Activity activity, Bundle extras) {
        Intent upIntent = NavUtils.getParentActivityIntent(activity);
        if (upIntent != null) {
            if (extras != null) {
                upIntent.putExtras(extras);
            }
            if (NavUtils.shouldUpRecreateTask(activity, upIntent)) {
                // This activity is NOT part of this app's task, so create a new task
                // when navigating up, with a synthesized back stack.
                TaskStackBuilder.create(activity)
                        // Add all of this activity's parents to the back stack.
                        .addNextIntentWithParentStack(upIntent)
                        // Navigate up to the closest parent.
                        .startActivities();
            } else {
                // This activity is part of this app's task, so simply
                // navigate up to the logical parent activity.
                // According to http://stackoverflow.com/a/14792752/2420519
                // NavUtils.navigateUpTo(activity, upIntent);
                upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(upIntent);
            }
        }
        activity.finish();
    }

    public static void navigateUp(Activity activity) {
        navigateUp(activity, null);
    }

    public static void setupActionBar(Activity activity) {
        ActionBar actionBar = activity.getActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
    }

    public static void setupActionBarDisplayUp(Activity activity) {
        setupActionBar(activity);
        activity.getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * 解析uri
     *
     * @param path
     * @return
     */
    private static Uri parseUri(String path) {
        Uri uri = null;
        File file = new File(path);
        if (file.exists()) {
            uri = Uri.fromFile(file);
        } else {
            uri = Uri.parse(path);
        }
        return uri;
    }

}
