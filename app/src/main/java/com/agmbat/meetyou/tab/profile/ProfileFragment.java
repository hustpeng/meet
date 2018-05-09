package com.agmbat.meetyou.tab.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.agmbat.android.AppResources;
import com.agmbat.android.image.BitmapUtils;
import com.agmbat.android.image.ImageManager;
import com.agmbat.imsdk.imevent.LoginUserUpdateEvent;
import com.agmbat.imsdk.user.LoginUser;
import com.agmbat.imsdk.user.UserManager;
import com.agmbat.meetyou.AboutActivity;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.account.ChangePasswordActivity;
import com.agmbat.meetyou.coins.CoinsActivity;
import com.agmbat.meetyou.helper.AvatarHelper;
import com.agmbat.meetyou.helper.GenderHelper;
import com.agmbat.meetyou.settings.PersonalInfoActivity;
import com.agmbat.wxshare.ShareContent;
import com.agmbat.wxshare.WXShare;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Tab 我的界面
 */
public class ProfileFragment extends Fragment {

    /**
     * 头像
     */
    @BindView(R.id.avatar)
    ImageView mAvatarView;

    /**
     * 昵称
     */
    @BindView(R.id.nickname)
    TextView mNickNameView;

    /**
     * 用户名
     */
    @BindView(R.id.user_name)
    TextView mUserNameView;

    /**
     * 性别
     */
    @BindView(R.id.gender)
    ImageView mGenderView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_profile, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        updateView(UserManager.getInstance().getLoginUser());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LoginUserUpdateEvent event) {
        LoginUser user = event.getLoginUser();
        updateView(user);
    }

    @OnClick(R.id.view_user)
    void onClickUser() {
        startActivity(new Intent(getActivity(), PersonalInfoActivity.class));
    }

    @OnClick(R.id.btn_change_password)
    void onClickChangePassword() {
        startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
    }

    @OnClick(R.id.btn_credits)
    void onClickCredits() {
        startActivity(new Intent(getActivity(), CoinsActivity.class));
    }

    @OnClick(R.id.btn_invite_friend)
    void onClickInviteFriend() {
        File file = new File(Environment.getExternalStorageDirectory(), "yuyuan.jpg");
        if (!file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            BitmapUtils.compressToFile(bitmap, file.getAbsolutePath());
        }
        ShareContent content = new ShareContent();
        content.mDescription = AppResources.getString(R.string.invite_friend_msg);
        List<File> fileList = new ArrayList<>();
        fileList.add(file);
        content.mImageFileList = fileList;
        WXShare.share(getActivity(), content);
    }

    @OnClick(R.id.btn_about)
    void onClickAbout() {
        startActivity(new Intent(getActivity(), AboutActivity.class));
    }

    private void updateView(LoginUser user) {
        mNickNameView.setText(user.getNickname());
        ImageManager.displayImage(user.getAvatar(), mAvatarView, AvatarHelper.getOptions());
        mUserNameView.setText(getString(R.string.id_name_format) + " " + user.getUserName());
        mGenderView.setImageResource(GenderHelper.getIconRes(user.getGender()));
    }

}
