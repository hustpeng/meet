package com.agmbat.meetyou.edituserinfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.agmbat.android.image.ImageManager;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.helper.AvatarHelper;
import com.agmbat.photoview.PhotoView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DisplayAvatarActivity extends Activity {

    private static final String EXTRA_AVATAR_URL = "avatar_url";

    @BindView(R.id.avatar)
    PhotoView mAvatarView;

    private String mAvatarUrl;

    public static void launch(Context context, String avatarUrl) {
        Intent intent = new Intent(context, DisplayAvatarActivity.class);
        intent.putExtra(EXTRA_AVATAR_URL, avatarUrl);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_avatar);
        ButterKnife.bind(this);
        handleIntent();
        initContentView();
    }

    private void handleIntent() {
        mAvatarUrl = getIntent().getStringExtra(EXTRA_AVATAR_URL);
    }

    private void initContentView() {
//        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(screenWidth, screenWidth);
//        params.gravity = Gravity.CENTER;
//        mAvatarView.setLayoutParams(params);
        ImageManager.displayImage(mAvatarUrl, mAvatarView, AvatarHelper.getRectangleUserOptions());
    }
}
