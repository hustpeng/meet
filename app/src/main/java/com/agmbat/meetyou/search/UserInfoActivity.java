package com.agmbat.meetyou.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.agmbat.android.image.ImageManager;
import com.agmbat.android.utils.ToastUtil;
import com.agmbat.android.utils.WindowUtils;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.data.ContactInfo;
import com.agmbat.imsdk.user.UserManager;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.helper.AvatarHelper;
import com.agmbat.meetyou.helper.GenderHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 用户信息界面
 */
public class UserInfoActivity extends Activity {

    @BindView(R.id.avatar)
    ImageView mAvatarView;

    @BindView(R.id.nickname)
    TextView mNickNameView;

    @BindView(R.id.gender)
    ImageView mGenderView;

    @BindView(R.id.username)
    TextView mUserNameView;

    private ContactInfo mContactInfo;

    /**
     * 查看用户详细信息
     *
     * @param context
     * @param contactInfo
     */
    public static void viewUserInfo(Context context, ContactInfo contactInfo) {
        UserManager.getInstance().addContactToCache(contactInfo);
        Intent intent = new Intent(context, UserInfoActivity.class);
        intent.putExtra("userInfo", contactInfo.getBareJid());
        context.startActivity(intent);
    }

    /**
     * 通过intent获取用户信息
     *
     * @param intent
     * @return
     */
    private static ContactInfo getContactInfo(Intent intent) {
        String jid = intent.getStringExtra("userInfo");
        return UserManager.getInstance().getContactFromCache(jid);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, 0xff232325);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        mContactInfo = getContactInfo(getIntent());
        if (mContactInfo == null) {
            ToastUtil.showToastLong("获取联系人信息失败");
            finish();
            return;
        }
        ImageManager.displayImage(mContactInfo.getAvatar(), mAvatarView, AvatarHelper.getOptions());
        mNickNameView.setText(mContactInfo.getNickName());
        mGenderView.setImageResource(GenderHelper.getIconRes(mContactInfo.getGender()));
        mUserNameView.setText(getString(R.string.id_name_format) + " " + mContactInfo.getUserName());
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    @OnClick(R.id.title_btn_back)
    void onClickBack() {
        finish();
    }

    /**
     * 点击添加联系人
     */
    @OnClick(R.id.btn_add_to_contact)
    void onClickAddToContact() {
        boolean result = XMPPManager.getInstance().getRosterManager().addContactToFriend(mContactInfo);
        if (result) {
            ToastUtil.showToastLong("已发送");
        } else {
            ToastUtil.showToastLong("添加好友失败");
        }
    }

    //    UserInfo mUserInfo;
//
//    @Bind(R.id.ibToolbarMore)
//    ImageButton mIbToolbarMore;
//

//    @Bind(R.id.tvAccount)
//    TextView mTvAccount;
//    @Bind(R.id.tvNickName)
//    TextView mTvNickName;
//    @Bind(R.id.tvArea)
//    TextView mTvArea;
//    @Bind(R.id.tvSignature)
//    TextView mTvSignature;
//
//    @Bind(R.id.oivAliasAndTag)
//    OptionItemView mOivAliasAndTag;
//    @Bind(R.id.llArea)
//    LinearLayout mLlArea;
//    @Bind(R.id.llSignature)
//    LinearLayout mLlSignature;
//
//    @Bind(R.id.btnCheat)
//    Button mBtnCheat;
//    @Bind(R.id.btnAddToContact)
//    Button mBtnAddToContact;
//
//    @Bind(R.id.rlMenu)
//    RelativeLayout mRlMenu;
//    @Bind(R.id.svMenu)
//    ScrollView mSvMenu;
//    @Bind(R.id.oivAlias)
//    OptionItemView mOivAlias;
//    @Bind(R.id.oivDelete)
//    OptionItemView mOivDelete;
//    private Friend mFriend;
//
//    @Override
//    public void init() {
//        Intent intent = getIntent();
//        mUserInfo = intent.getExtras().getParcelable("userInfo");
//        registerBR();
//    }
//
//    @Override
//    public void initView() {
//        if (mUserInfo == null) {
//            finish();
//            return;
//        }
//
//        mIbToolbarMore.setVisibility(View.VISIBLE);
//    }
//
//    @Override
//    public void initData() {
//        mFriend = DBManager.getInstance().getFriendById(mUserInfo.getUserId());
//        Glide.with(this).load(DBManager.getInstance().getPortraitUri(mUserInfo)).centerCrop().into(mIvHeader);
//        mTvAccount.setText(UIUtils.getString(R.string.my_chat_account, mUserInfo.getUserId()));
//        mTvName.setText(mUserInfo.getName());
//
//        if (mFriend == null) {//陌生人
//            mBtnCheat.setVisibility(View.GONE);
//            mBtnAddToContact.setVisibility(View.VISIBLE);
//            mTvNickName.setVisibility(View.INVISIBLE);
//        } else {
//            if (DBManager.getInstance().isMe(mFriend.getUserId())) {//我
//                mTvNickName.setVisibility(View.INVISIBLE);
//                mOivAliasAndTag.setVisibility(View.GONE);
//                mLlArea.setVisibility(View.GONE);
//                mLlSignature.setVisibility(View.GONE);
//            } else if (DBManager.getInstance().isMyFriend(mFriend.getUserId())) {//我的朋友
//                String nickName = mFriend.getDisplayName();
//                mTvName.setText(nickName);
//                if (TextUtils.isEmpty(nickName)) {
//                    mTvNickName.setVisibility(View.INVISIBLE);
//                } else {
//                    mTvNickName.setText(UIUtils.getString(R.string.nickname_colon, mFriend.getName()));
//                }
//            } else {//陌生人
//                mBtnCheat.setVisibility(View.GONE);
//                mBtnAddToContact.setVisibility(View.VISIBLE);
//                mTvNickName.setVisibility(View.INVISIBLE);
//            }
//        }
//    }
//
//    @Override
//    public void initListener() {
//        mIbToolbarMore.setOnClickListener(v -> showMenu());
//        mOivAliasAndTag.setOnClickListener(v -> jumpToSetAlias());
//
//        mBtnCheat.setOnClickListener(v -> {
//            Intent intent = new Intent(UserInfoActivity.this, SessionActivity.class);
//            intent.putExtra("sessionId", mUserInfo.getUserId());
//            intent.putExtra("sessionType", SessionActivity.SESSION_TYPE_PRIVATE);
//            jumpToActivity(intent);
//            finish();
//        });
//
//        mBtnAddToContact.setOnClickListener(v -> {
//            //跳转到写附言界面
//            Intent intent = new Intent(UserInfoActivity.this, PostScriptActivity.class);
//            intent.putExtra("userId", mUserInfo.getUserId());
//            jumpToActivity(intent);
//        });
//
//        mRlMenu.setOnClickListener(v -> hideMenu());
//
//        mOivAlias.setOnClickListener(v -> {
//            jumpToSetAlias();
//            hideMenu();
//        });
//        mOivDelete.setOnClickListener(v -> {
//            hideMenu();
//            showMaterialDialog(UIUtils.getString(R.string.delete_contact),
//                    UIUtils.getString(R.string.delete_contact_content, mUserInfo.getName()),
//                    UIUtils.getString(R.string.delete),
//                    UIUtils.getString(R.string.cancel),
//                    v1 -> ApiRetrofit.getInstance()
//                            .deleteFriend(mUserInfo.getUserId())
//                            .subscribeOn(Schedulers.io())
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe(deleteFriendResponse -> {
//                                hideMaterialDialog();
//                                if (deleteFriendResponse.getCode() == 200) {
//                                    RongIMClient.getInstance().getConversation(Conversation.ConversationType.PRIVATE, mUserInfo.getUserId(), new RongIMClient.ResultCallback<Conversation>() {
//                                        @Override
//                                        public void onSuccess(Conversation conversation) {
//                                            RongIMClient.getInstance().clearMessages(Conversation.ConversationType.PRIVATE, mUserInfo.getUserId(), new RongIMClient.ResultCallback<Boolean>() {
//                                                @Override
//                                                public void onSuccess(Boolean aBoolean) {
//                                                    RongIMClient.getInstance().removeConversation(Conversation.ConversationType.PRIVATE, mUserInfo.getUserId(), null);
//                                                }
//
//                                                @Override
//                                                public void onError(RongIMClient.ErrorCode errorCode) {
//
//                                                }
//                                            });
//                                        }
//
//                                        @Override
//                                        public void onError(RongIMClient.ErrorCode errorCode) {
//
//                                        }
//                                    });
//                                    //通知对方被删除(把我的id发给对方)
//                                    DeleteContactMessage deleteContactMessage = DeleteContactMessage.obtain(UserCache.getId());
//                                    RongIMClient.getInstance().sendMessage(Message.obtain(mUserInfo.getUserId(), Conversation.ConversationType.PRIVATE, deleteContactMessage), "", "", null, null);
//                                    DBManager.getInstance().deleteFriendById(mUserInfo.getUserId());
//                                    UIUtils.showToast(UIUtils.getString(R.string.delete_success));
//                                    BroadcastManager.getInstance(UserInfoActivity.this).sendBroadcast(AppConst.UPDATE_FRIEND);
//                                    finish();
//                                } else {
//                                    UIUtils.showToast(UIUtils.getString(R.string.delete_fail));
//                                }
//                            }, this::loadError)
//                    , v2 -> hideMaterialDialog());
//        });
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        unRegisterBR();
//    }
//
//    private void loadError(Throwable throwable) {
//        hideMaterialDialog();
//        LogUtils.sf(throwable.getLocalizedMessage());
//    }
//
//    private void jumpToSetAlias() {
//        Intent intent = new Intent(this, SetAliasActivity.class);
//        intent.putExtra("userId", mUserInfo.getUserId());
//        jumpToActivity(intent);
//    }
//
//    private void showMenu() {
//        mRlMenu.setVisibility(View.VISIBLE);
//        TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0);
//        ta.setDuration(200);
//        mSvMenu.startAnimation(ta);
//    }
//
//    private void hideMenu() {
//        TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);
//        ta.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                mRlMenu.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//        ta.setDuration(200);
//        mSvMenu.startAnimation(ta);
//    }
//
//    private void registerBR() {
//        BroadcastManager.getInstance(this).register(AppConst.CHANGE_INFO_FOR_USER_INFO, new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                mUserInfo = DBManager.getInstance().getUserInfo(mUserInfo.getUserId());
//                initData();
//            }
//        });
//    }
//
//    private void unRegisterBR() {
//        BroadcastManager.getInstance(this).unregister(AppConst.CHANGE_INFO_FOR_USER_INFO);
//    }
//
//    @Override
//    protected BasePresenter createPresenter() {
//        return null;
//    }
//
//    @Override
//    protected int provideContentViewId() {
//        return R.layout.activity_user_info;
//    }
}
