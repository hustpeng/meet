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

import com.agmbat.android.permissions.PermissionArrayAction;
import com.agmbat.android.permissions.Permissions;
import com.agmbat.android.utils.WindowUtils;
import com.agmbat.imsdk.R;

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
            Intent intent = new Intent();
            intent.setClassName(getPackageName(), SplashManager.getMainClassName());
            startActivity(intent);
            finish();
        }
    };


    private ViewPager mViewPager;

    private TextView mEntryButton;

    private ImageView mSplashImageView;

    private TextView mSkipButton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, 0xff232325);
        setContentView(R.layout.activity_splash);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mEntryButton = (TextView) findViewById(R.id.btn_entry);
        mSplashImageView = (ImageView) findViewById(R.id.splash_image);
        mSkipButton = (TextView) findViewById(R.id.btn_skip);
        if (isFirstLaunch()) {
            showGuide();
            // 保存不是第一次启动
            SharedPreferences preferences = getSharedPreferences("splash_launch", Context.MODE_PRIVATE);
            preferences.edit().putBoolean("splash_launch_first", false).commit();
        } else {
            showSplash();
        }
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
        mEntryButton.setVisibility(View.INVISIBLE);
        mSplashImageView.setVisibility(View.INVISIBLE);
        mSkipButton.setVisibility(View.INVISIBLE);

        final List<Integer> list = new ArrayList<>();
        list.add(R.drawable.guide1);
        list.add(R.drawable.guide2);
        list.add(R.drawable.guide3);
        mViewPager.setAdapter(new ViewPagerAdapter(this, list));
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == list.size() - 1) {
                    mEntryButton.setVisibility(View.VISIBLE);
                } else {
                    mEntryButton.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSplash();
            }
        });
    }

    /**
     * 显示闪屏页面
     */
    private void showSplash() {
        mViewPager.setVisibility(View.GONE);
        mEntryButton.setVisibility(View.GONE);
        mSplashImageView.setVisibility(View.VISIBLE);
        mSkipButton.setVisibility(View.INVISIBLE);

        mSplashImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        SplashManager.displaySplash(mSplashImageView);
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
                    mHandler.postDelayed(mRunnable, 3000);
                } else {
                    showNoPermissionDialog();
                }
            }
        });
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
     * 是否第一次启动
     *
     * @return
     */
    private boolean isFirstLaunch() {
        SharedPreferences preferences = getSharedPreferences("splash_launch", Context.MODE_PRIVATE);
        return preferences.getBoolean("splash_launch_first", true);
    }

    private static class ViewPagerAdapter extends PagerAdapter {

        private Context mContext;
        private List<Integer> mDataList;

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
            ImageView view = new ImageView(mContext);
            view.setImageResource((mDataList.get(position)));
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
