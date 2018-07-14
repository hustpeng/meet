package com.agmbat.android;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import com.agmbat.file.ZipUtils;
import com.agmbat.io.IoUtils;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * 提供当前app的资源
 */
public class AppResources {

    private static Context sAppContext;
    private static Resources sRes;
    private static String sPackageName;
    private static AssetManager sAssetManager;

    public static void init(Context context) {
        sAppContext = context.getApplicationContext();
        sRes = context.getResources();
        sPackageName = context.getPackageName();
        sAssetManager = context.getAssets();
    }

    /**
     * Return the context of the single, global Application object of the current process.
     *
     * @return The application Context.
     */
    public static Context getAppContext() {
        return sAppContext;
    }

    /**
     * Return a Resources instance for your application's package.
     *
     * @return
     */
    public static Resources getResources() {
        return sRes;
    }

    public static int getAnimId(String name) {
        return sRes.getIdentifier(name, "anim", sPackageName);
    }

    public static int getDimenId(String name) {
        return sRes.getIdentifier(name, "dimen", sPackageName);
    }

    public static int getDimensionPixelSize(String name) {
        int id = getDimenId(name);
        return sRes.getDimensionPixelSize(id);
    }

    /**
     * find the component id(int value) for the given component name in current content view
     *
     * @param name
     * @return
     */
    public static int getViewId(String name) {
        return sRes.getIdentifier(name, "id", sPackageName);
    }

    /**
     * find the layout resource id for the given layout file name in layout folder
     *
     * @param name
     * @return
     */
    public static int getLayoutId(String name) {
        return sRes.getIdentifier(name, "layout", sPackageName);
    }

    /**
     * 返回drawable或者mipmap
     *
     * @param emojiName
     * @return
     */
    public static Drawable getDrawableOrMipmap(String emojiName) {
        int resID = sRes.getIdentifier(emojiName, "mipmap", sPackageName);
        if (resID <= 0) {
            resID = sRes.getIdentifier(emojiName, "drawable", sPackageName);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return sRes.getDrawable(resID, null);
        } else {
            return sRes.getDrawable(resID);
        }
    }

    /**
     * find the drawable resource id for the given drawable name in drawable folder
     *
     * @param name the name of drawable
     * @return drawable id
     */
    public static int getDrawableId(String name) {
        return sRes.getIdentifier(name, "drawable", sPackageName);
    }

    /**
     * 获取mipmap资源id
     *
     * @param name
     * @return
     */
    public static int getMipmapId(String name) {
        return sRes.getIdentifier(name, "mipmap", sPackageName);
    }

    /**
     * get drawable object from given drawable file name
     *
     * @param name
     * @return
     */
    public static Drawable getDrawable(String name) {
        int id = getDrawableId(name);
        return sRes.getDrawable(id);
    }

    public static Drawable getDrawable(int emoticon) {
        if (emoticon <= 0) {
            return null;
        }
        try {
            return Build.VERSION.SDK_INT >= 21 ? sRes.getDrawable(emoticon, (Resources.Theme) null) : sRes.getDrawable(emoticon);
        } catch (Exception var4) {
            var4.printStackTrace();
            return null;
        }
    }


    /**
     * get styleable id for the given styleable name
     *
     * @param name
     * @return
     */
    public static int getStyleId(String name) {
        return sRes.getIdentifier(name, "style", sPackageName);
    }

    /**
     * find the color resource id for the given color name
     *
     * @param name
     * @return
     */
    public static int getColorId(final String name) {
        return sRes.getIdentifier(name, "color", sPackageName);
    }

    public static int getColor(int id) {
        return sRes.getColor(id);
    }

    /**
     * get color value for the given color name
     *
     * @param name
     * @return
     */
    public static int getColor(String name) {
        int id = getColorId(name);
        return sRes.getColor(id);
    }

    /**
     * get charsequence for the given text name
     *
     * @param resId
     * @return
     */
    public static CharSequence getText(int resId) {
        return sRes.getText(resId);
    }

    /**
     * retrieve the string value for the given string name
     *
     * @param name
     * @return
     */
    public CharSequence getText(final String name) {
        final int stringId = getStringId(name);
        return getText(stringId);
    }

    public static String getString(int id) {
        return sRes.getString(id);
    }

    public static String getString(int id, Object... formatArgs) {
        return sRes.getString(id, formatArgs);
    }

    /**
     * get string value from given string name
     *
     * @param name
     * @return
     */
    public static String getString(String name) {
        int id = getStringId(name);
        return sRes.getString(id);
    }

    /**
     * get string value from given sting name,this string is formatd by some formater
     *
     * @param name
     * @param formatArgs
     * @return
     */
    public static String getString(String name, Object... formatArgs) {
        int id = getStringId(name);
        return sRes.getString(id, formatArgs);
    }

    public static String[] getStringArray(int id) {
        return sRes.getStringArray(id);
    }

    /**
     * find the string resource id for the given component name in strings.xml file
     *
     * @param name
     * @return
     */
    public static int getStringId(String name) {
        return sRes.getIdentifier(name, "string", sPackageName);
    }

    /**
     * retrieve a view object through the component name
     *
     * @param v
     * @param name
     * @return
     */
    public static View findViewByIdName(View v, String name) {
        int id = getViewId(name);
        return v.findViewById(id);
    }

    /**
     * inflate a view object from the given file name
     *
     * @param layoutName resource file name for the view
     * @return
     */
    public static View inflateView(String layoutName) {
        return View.inflate(sAppContext, getLayoutId(layoutName), null);
    }

    /**
     * inflate a view object from the given file name
     *
     * @param layoutName resource file name for the view
     * @param root       root for inflate on
     * @return
     */
    public static View inflateView(String layoutName, final ViewGroup root) {
        return View.inflate(sAppContext, getLayoutId(layoutName), root);
    }

    /**
     * Create a new typeface from the specified font data.
     *
     * @param path The file name of the font data in the assets directory
     * @return The new typeface.
     */
    public static Typeface createTypeface(String path) {
        return Typeface.createFromAsset(sAssetManager, path);
    }

    /**
     * 打开Asset资源
     *
     * @param assetPath
     * @return
     */
    public static InputStream openAssetFile(String assetPath) {
        try {
            return sAssetManager.open(assetPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long getAssetFileSize(String name) {
        InputStream is = openAssetFile(name);
        if (is != null) {
            try {
                return is.available();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IoUtils.closeQuietly(is);
            }
        }
        return -1;
    }


    /**
     * 从 asset中获取drawable
     *
     * @param assetPath
     * @return
     */
    public static Drawable getAssetDrawable(String assetPath) {
        Bitmap bitmap = AppResources.getAssetBitmap(assetPath);
        return new BitmapDrawable(bitmap);
    }

    public static Bitmap getAssetBitmap(String assetPath) {
        if (TextUtils.isEmpty(assetPath)) {
            return null;
        }
        InputStream is = openAssetFile(assetPath);
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        IoUtils.closeQuietly(is);
        return bitmap;
    }

    /**
     * Read a raw file from the android application package
     *
     * @param assetPath The assetPath to read
     * @return The whole file in a string
     */
    public static String readAssetFile(String assetPath) {
        if (TextUtils.isEmpty(assetPath)) {
            return null;
        }
        InputStream is = openAssetFile(assetPath);
        if (is != null) {
            try {
                return IoUtils.loadContent(is);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IoUtils.closeQuietly(is);
            }
        }
        return null;
    }

    public static boolean copyAssetFile(String name, String dest) {
        return copyAssetFile(name, new File(dest));
    }

    public static boolean copyAssetFile(String name, File file) {
        InputStream is = openAssetFile(name);
        if (is == null) {
            return false;
        }
        try {
            IoUtils.copyStream(is, file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(is);
        }
        return false;
    }

    /**
     * 打开raw资源
     *
     * @param rawId
     * @return
     */
    public static InputStream openRawResource(int rawId) {
        return sRes.openRawResource(rawId);
    }

    /**
     * Read a raw file from the android application package
     *
     * @param resId The resource ID to read
     * @return The whole file in a string
     */
    public static String readRawFile(int resId) {
        InputStream is = sRes.openRawResource(resId);
        try {
            return IoUtils.loadContent(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(is);
        }
        return null;
    }

    public static boolean copyRawFile(int rawId, String dest) {
        return copyRawFile(rawId, new File(dest));
    }

    /**
     * 复制raw文件到指定文件
     *
     * @param rawId
     * @param file
     * @return
     */
    public static boolean copyRawFile(int rawId, File file) {
        InputStream is = openRawResource(rawId);
        if (is == null) {
            return false;
        }
        try {
            IoUtils.copyStream(is, file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(is);
        }
        return false;
    }

    /**
     * 读取apk中META-INF/下指定的文件
     *
     * @param path
     * @return
     */
    public static String readMetaInfFile(String path) {
        String sourceDir = sAppContext.getApplicationInfo().sourceDir;
        String name = "META-INF/" + path;
        return ZipUtils.readZipFileText(sourceDir, name);
    }

    /**
     * Update the application locale based on the language
     *
     * @param language
     */
    public static void updateLocale(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        sRes.updateConfiguration(configuration, sRes.getDisplayMetrics());
    }

    public static boolean isHardwareAccelerated(Canvas canvas) {
        if (Build.VERSION.SDK_INT >= 11) {
            try {
                return (Boolean) Canvas.class.getDeclaredMethod("isHardwareAccelerated").invoke(canvas);
            } catch (Exception e) {
                // ignore exception
            }
        }
        return false;
    }

    /**
     * 去掉ContentView中的前景色(黑色的阴影)
     *
     * @param activity
     */
    public static void disableContentForeground(Activity activity) {
        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
        content.setForeground(null);
    }


}
