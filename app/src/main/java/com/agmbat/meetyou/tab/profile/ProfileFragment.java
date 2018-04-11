package com.agmbat.meetyou.tab.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.agmbat.android.image.ImageManager;
import com.agmbat.imsdk.IM;
import com.agmbat.imsdk.account.ImAccountManager;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.account.ChangePasswordActivity;
import com.agmbat.meetyou.coins.CoinsActivity;
import com.agmbat.meetyou.settings.PersonalInfoActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smackx.vcard.VCardObject;

/**
 * Tab 我的界面
 */
public class ProfileFragment extends Fragment {

    /**
     * 头像
     */
    private ImageView mHeadView;

    /**
     * 昵称
     */
    private TextView mNickNameView;

    /**
     * 用户名
     */
    private TextView mUserNameView;

    /**
     * 性别
     */
    private ImageView mGenderView;

    /**
     * 用户信息
     */
    private VCardObject mVCardObject;

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
        mUserNameView = (TextView) view.findViewById(R.id.user_name);
        mNickNameView = (TextView) view.findViewById(R.id.nickname);
        mHeadView = (ImageView) view.findViewById(R.id.head);
        mGenderView = (ImageView) view.findViewById(R.id.gender);
        view.findViewById(R.id.view_user).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PersonalInfoActivity.class));
                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        view.findViewById(R.id.btn_change_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        view.findViewById(R.id.btn_credits).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CoinsActivity.class));
                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        updateView();
        IM.get().fetchMyVCard();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(VCardObject vCardObject) {
        mVCardObject = vCardObject;
        updateView();
    }

    private void updateView() {
        if (mVCardObject == null) {
            return;
        }
        mNickNameView.setText(mVCardObject.getNickname());
        ImageManager.displayImage(mVCardObject.getAvatar(), mHeadView);
        mUserNameView.setText(getString(R.string.id_name_format) + " " + mVCardObject.getUserName());
        mGenderView.setImageResource(getGenderImage(mVCardObject.getGender()));
    }

    private int getGenderImage(int gender) {
        if (gender == 1) {
            return R.drawable.im_ic_gender_female;
        }
        return R.drawable.im_ic_gender_male;
    }
}
