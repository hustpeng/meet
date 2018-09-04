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
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.imevent.LoginUserUpdateEvent;
import com.agmbat.imsdk.user.LoginUser;
import com.agmbat.log.Debug;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.account.ChangePasswordActivity;
import com.agmbat.meetyou.account.LoginActivity;
import com.agmbat.meetyou.coins.CoinsActivity;
import com.agmbat.meetyou.edituserinfo.PersonalInfoActivity;
import com.agmbat.meetyou.helper.AvatarHelper;
import com.agmbat.meetyou.helper.GenderHelper;
import com.agmbat.meetyou.settings.AboutActivity;
import com.agmbat.meetyou.settings.IdentityAuthenticationActivity;
import com.agmbat.meetyou.settings.NotificationSettingActivity;
import com.agmbat.meetyou.settings.PrivateSettingActivity;
import com.agmbat.meetyou.util.ResourceUtil;
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
        updateView(XMPPManager.getInstance().getRosterManager().getLoginUser());
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

    @OnClick(R.id.btn_new_msg)
    void onClickNewMsgSetting() {
        NotificationSettingActivity.launch(getContext());
    }

    @OnClick(R.id.btn_privacy)
    void onClickPrivateSetting(){
        PrivateSettingActivity.launch(getContext());
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
        LoginUser loginUser = XMPPManager.getInstance().getRosterManager().getLoginUser();
        content.mDescription = AppResources.getString(R.string.invite_friend_msg, String.valueOf(loginUser.getImUid()));
        List<File> fileList = new ArrayList<>();
        fileList.add(file);
        content.mImageFileList = fileList;
        WXShare.share(getActivity(), content);
    }

    @OnClick(R.id.btn_about)
    void onClickAbout() {
        startActivity(new Intent(getActivity(), AboutActivity.class));
    }

    /**
     * 点击身份认证
     */
    @OnClick(R.id.identity_authentication)
    void onClickIdentityAuthentication() {
        startActivity(new Intent(getActivity(), IdentityAuthenticationActivity.class));
    }

    /**
     * 点击退出登录
     */
    @OnClick(R.id.btn_exit)
    void onClickExit() {
        // TODO 显示对话框, 需要清空数据
        XMPPManager.getInstance().logout();
        XMPPManager.getInstance().getRosterManager().resetData();
        XMPPManager.getInstance().getMessageManager().clearCachedMessages();
        getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();
    }

    private void updateView(LoginUser user) {
        if (user == null || !user.isValid()) {
            Debug.printStackTrace();
            return;
        }
        mNickNameView.setText(user.getNickname());
        ImageManager.displayImage(user.getAvatar(), mAvatarView, AvatarHelper.getOptions());
        mUserNameView.setText(ResourceUtil.getString(R.string.id_name_format, user.getImUid()));
        mGenderView.setImageResource(GenderHelper.getIconRes(user.getGender()));
    }

}
