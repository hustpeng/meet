package com.agmbat.android.image;

import android.content.Context;
import android.widget.ImageView;

import com.agmbat.android.AppResources;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.core.download.Scheme;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;

/**
 * 图片管理,配置ImageLoader
 */
public class ImageManager {

    /**
     * 默认图片显示
     */
    private static DisplayImageOptions sDefaultOptions;

    /**
     * app程序显示
     */
    private static DisplayImageOptions sPackageIconOptions = null;

    /**
     * 圆形图片显示
     */
    private static DisplayImageOptions sCircleOptions = null;

    public static void initImageLoader(Context context) {
        initImageLoader(context, null);
    }

    public static void initImageLoader(Context context, ImageDownloader downloader) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(context);
        builder.threadPriority(Thread.NORM_PRIORITY - 2);
        builder.threadPoolSize(10);
        builder.denyCacheImageMultipleSizesInMemory();
        builder.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        builder.diskCacheSize(50 * 1024 * 1024); // 50 Mb
        builder.tasksProcessingOrder(QueueProcessingType.FIFO); // QueueProcessingType.LIFO
        builder.writeDebugLogs(); // Remove for release app
        builder.imageDownloader(downloader);
        ImageLoaderConfiguration config = builder.build();
        ImageLoader.getInstance().init(config);
    }

    /**
     * 获取默认图片显示
     *
     * @return
     */
    private static DisplayImageOptions getDefaultOptions() {
        if (sDefaultOptions == null) {
            sDefaultOptions = initDefaultOptions();
        }
        return sDefaultOptions;
    }

    /**
     * 获取包图片显示的配置
     *
     * @return
     */
    private static DisplayImageOptions getPackageIconOptions() {
        if (sPackageIconOptions == null) {
            sPackageIconOptions = initPackageIconOptions();
        }
        return sPackageIconOptions;
    }

    /**
     * 获取圆形图片显示配置
     *
     * @return
     */
    public static DisplayImageOptions getCircleOptions() {
        if (sCircleOptions == null) {
            sCircleOptions = initCircleOptions();
        }
        return sCircleOptions;
    }

    /**
     * 显示图片
     *
     * @param uri
     * @param imageView
     */
    public static void displayImage(String uri, ImageView imageView) {
        ImageLoader.getInstance().displayImage(uri, imageView, getDefaultOptions());
    }

    /**
     * 使用自定义显示参数显示图片
     *
     * @param uri
     * @param imageView
     * @param options
     */
    public static void displayImage(String uri, ImageView imageView, DisplayImageOptions options) {
        ImageLoader.getInstance().displayImage(uri, imageView, options);
    }

    public static void displayImage(String uri, ImageView imageView, ImageLoadingListener l) {
        ImageLoader.getInstance().displayImage(uri, imageView, getDefaultOptions(), l);
    }

    /**
     * 显示包图片
     *
     * @param packageName
     * @param imageView
     */
    public static void displayPackageIcon(String packageName, ImageView imageView) {
        String uri = Scheme.wrapUri("package", packageName);
        displayImage(uri, imageView, getPackageIconOptions());
    }

    /**
     * 获取磁盘缓存的图片文件
     *
     * @param uri
     * @return
     */
    public static File getDiskCacheFile(String uri) {
        return ImageLoader.getInstance().getDiskCache().get(uri);
    }


    /**
     * 初始化显示参数
     *
     * @return
     */
    private static DisplayImageOptions initDefaultOptions() {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        // builder.showImageOnLoading(R.drawable.ic_stub);
        int drawableId = AppResources.getDrawableId("ic_launcher");
        builder.showImageForEmptyUri(drawableId);
        builder.showImageOnFail(drawableId);
        builder.cacheInMemory(true);
        builder.cacheOnDisk(true);
        builder.considerExifParams(false);
        builder.displayer(new SimpleBitmapDisplayer()); // new RoundedBitmapDisplayer(20)
        DisplayImageOptions options = builder.build();
        return options;
    }

    /**
     * 初始化apk图片显示的参数
     *
     * @return
     */
    private static DisplayImageOptions initPackageIconOptions() {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        // builder.showImageOnLoading(R.drawable.ic_stub);
        builder.showImageForEmptyUri(android.R.drawable.sym_def_app_icon);
        builder.showImageOnFail(android.R.drawable.sym_def_app_icon);
        builder.cacheInMemory(true);
        builder.cacheOnDisk(true);
        builder.considerExifParams(false);
        builder.displayer(new SimpleBitmapDisplayer());
        // new RoundedBitmapDisplayer(20);
        DisplayImageOptions options = builder.build();
        return options;
    }


    /**
     * 初始化显示圆形图片参数
     *
     * @return
     */
    private static DisplayImageOptions initCircleOptions() {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        // builder.showImageOnLoading(R.drawable.ic_stub);
        int drawableId = AppResources.getDrawableId("ic_launcher");
        builder.showImageForEmptyUri(drawableId);
        builder.showImageOnFail(drawableId);
        builder.cacheInMemory(true);
        builder.cacheOnDisk(true);
        builder.considerExifParams(false);
        builder.displayer(new CircleBitmapDisplayer());
        DisplayImageOptions options = builder.build();
        return options;
    }

}

