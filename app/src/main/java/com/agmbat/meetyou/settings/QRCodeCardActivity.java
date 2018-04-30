package com.agmbat.meetyou.settings;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.agmbat.android.AppResources;
import com.agmbat.android.image.ImageManager;
import com.agmbat.android.utils.WindowUtils;
import com.agmbat.imsdk.imevent.LoginUserUpdateEvent;
import com.agmbat.imsdk.user.LoginUser;
import com.agmbat.imsdk.user.UserManager;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.helper.GenderHelper;
import com.google.zxing.client.android.encode.QRCodeEncoder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 我的二维码界面
 */
public class QRCodeCardActivity extends Activity {

    @BindView(R.id.qr_code)
    ImageView mQrCodeView;

    /**
     * 头像
     */
    @BindView(R.id.avatar)
    ImageView mAvatarView;

    @BindView(R.id.nickname)
    TextView mNickNameView;

    @BindView(R.id.gender)
    ImageView mGenderView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, 0xff232325);
        setContentView(R.layout.activity_qr_code_card);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        update(UserManager.getInstance().getLoginUser());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    /**
     * 收到vcard更新信息
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LoginUserUpdateEvent event) {
        LoginUser user = event.getLoginUser();
        update(user);
    }

    /**
     * 点击返回键
     */
    @OnClick(R.id.title_btn_back)
    void onClickBack() {
        finish();
    }

    private void update(LoginUser user) {
        String text = user.getUserName();
        int dimension = (int) AppResources.dipToPixel(250);
        Bitmap bitmap = QRCodeEncoder.encode(text, dimension);
        mQrCodeView.setImageBitmap(bitmap);
        mNickNameView.setText(user.getNickname());
        ImageManager.displayImage(user.getAvatar(), mAvatarView, ImageManager.getCircleOptions());
        mGenderView.setImageResource(GenderHelper.getIconRes(user.getGender()));
    }

}
