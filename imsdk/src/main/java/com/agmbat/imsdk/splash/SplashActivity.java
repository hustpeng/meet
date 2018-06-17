package com.agmbat.imsdk.splash;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.agmbat.android.image.ImageManager;
import com.agmbat.android.permissions.PermissionArrayAction;
import com.agmbat.android.permissions.Permissions;
import com.agmbat.android.utils.WindowUtils;
import com.agmbat.imsdk.R;
import com.agmbat.text.StringUtils;
import com.nostra13.universalimageloader.core.download.Scheme;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 闪屏页面, 支持显示引导页
 */
public class SplashActivity extends Activity {

    private Handler mHandler = new Handler();

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.removeCallbacks(mRunnable);
            entryMainPager();
        }
    };


    private ViewPager mViewPager;

    private ImageView mSplashImageView;

    /**
     * 跳过button
     */
    private TextView mSkipButton;

    /**
     * splash 显示时长
     */
    private long mSplashShowTime = 3000;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, 0xff232325);
        setContentView(R.layout.activity_splash);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mSplashImageView = (ImageView) findViewById(R.id.splash_image);
        mSkipButton = (TextView) findViewById(R.id.btn_skip);
        if (isFirstLaunch()) {
            showGuide();
        } else {
            showSplash();
        }
        Permissions.request(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        }, new PermissionArrayAction() {
            @Override
            public void onResult(String[] permissions, boolean[] grantResults) {
                if (Permissions.checkResult(grantResults)) {
                    if (mViewPager.isShown()) {
                        // 如果显示了引导页面, 则不处理
                    } else {
                        mHandler.postDelayed(mRunnable, mSplashShowTime);
                    }
                } else {
                    showNoPermissionDialog();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mHandler.removeCallbacks(mRunnable);
    }

    /**
     * 显示引导页
     */
    private void showGuide() {
        mViewPager.setVisibility(View.VISIBLE);
        mSplashImageView.setVisibility(View.INVISIBLE);
        mSkipButton.setVisibility(View.INVISIBLE);

        final List<Integer> list = new ArrayList<>();
        list.add(R.drawable.guide1);
        list.add(R.drawable.guide2);
        list.add(R.drawable.guide3);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this, list);
        adapter.setOnEntryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFirstLaunch();
                // 进入
                entryMainPager();
            }
        });
        mViewPager.setAdapter(adapter);
    }

    /**
     * 显示闪屏页面
     */
    private void showSplash() {
        mViewPager.setVisibility(View.GONE);
        mSplashImageView.setVisibility(View.VISIBLE);

        mSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.removeCallbacks(mRunnable);
                entryMainPager();
            }
        });

        mSplashImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        SplashInfo splashInfo = SplashManager.getDisplaySplash();
        ImageManager.displayImage(splashInfo.mImageUrl, mSplashImageView);
        if (splashInfo.canSkip) {
            mSkipButton.setVisibility(View.VISIBLE);
        } else {
            mSkipButton.setVisibility(View.INVISIBLE);
        }
        mSplashShowTime = splashInfo.displayTime;
        SplashManager.update();
    }

    /**
     * 显示没有权限的对话框并退出
     */
    private void showNoPermissionDialog() {
        new AlertDialog.Builder(this)
                .setMessage("请允许相关权限")
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }

    /**
     * 进入主页面
     */
    private void entryMainPager() {
        Intent intent = new Intent();
        intent.setClassName(getPackageName(), SplashManager.getMainClassName());
        startActivity(intent);
        finish();
    }

    /**
     * 是否第一次启动
     *
     * @return
     */
    private boolean isFirstLaunch() {
        SharedPreferences preferences = getSharedPreferences("splash_launch", Context.MODE_PRIVATE);
        return preferences.getBoolean("splash_launch_first", true);
    }

    /**
     * 保存不是第一次启动
     */
    private void saveFirstLaunch() {
        SharedPreferences preferences = getSharedPreferences("splash_launch", Context.MODE_PRIVATE);
        preferences.edit().putBoolean("splash_launch_first", false).commit();
    }

    private static class ViewPagerAdapter extends PagerAdapter {

        private Context mContext;
        private List<Integer> mDataList;

        private View.OnClickListener mOnEntryListener;

        public ViewPagerAdapter(Context context, List<Integer> list) {
            mContext = context;
            mDataList = list;
        }

        @Override
        public int getCount() {
            return mDataList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = View.inflate(mContext, R.layout.guide_item, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.icon);
            imageView.setImageResource((mDataList.get(position)));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            // 引导页进入的Button
            TextView entryButton = (TextView) view.findViewById(R.id.btn_entry);
            if (position == 2) {
                entryButton.setVisibility(View.VISIBLE);
                entryButton.setOnClickListener(mOnEntryListener);
            } else {
                entryButton.setVisibility(View.GONE);
            }
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        public void setOnEntryListener(View.OnClickListener l) {
            mOnEntryListener = l;
        }
    }
}
