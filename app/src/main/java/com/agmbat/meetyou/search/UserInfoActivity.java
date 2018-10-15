package com.agmbat.meetyou.search;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.agmbat.android.image.ImageManager;
import com.agmbat.android.utils.ToastUtil;
import com.agmbat.android.utils.UiUtils;
import com.agmbat.android.utils.WindowUtils;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.asmack.api.OnFetchContactListener;
import com.agmbat.imsdk.asmack.api.OnFetchLoginUserListener;
import com.agmbat.imsdk.asmack.api.XMPPApi;
import com.agmbat.imsdk.asmack.roster.ContactDBCache;
import com.agmbat.imsdk.asmack.roster.ContactInfo;
import com.agmbat.imsdk.imevent.ContactOnAddEvent;
import com.agmbat.imsdk.user.LoginUser;
import com.agmbat.log.Log;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.chat.ChatActivity;
import com.agmbat.meetyou.edituserinfo.DisplayAvatarActivity;
import com.agmbat.meetyou.helper.AvatarHelper;
import com.agmbat.meetyou.helper.GenderHelper;
import com.agmbat.meetyou.helper.UserInfoDisplay;
import com.agmbat.meetyou.util.ResourceUtil;
import com.agmbat.menu.MenuInfo;
import com.agmbat.menu.OnClickMenuListener;
import com.agmbat.menu.PopupMenu;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.List;

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

    @BindView(R.id.auth_status)
    ImageView mAuthView;

    @BindView(R.id.im_uid)
    TextView mImUidView;

    @BindView(R.id.label_gender)
    TextView mGenderTv;

    @BindView(R.id.age)
    TextView mAgeTv;

    @BindView(R.id.setup_alias)
    TextView mAliasTv;

    @BindView(R.id.more_user_info)
    TextView mMoreUserInfo;

    private ContactInfo mContactInfo;
    private BusinessHandler mBusinessHandler;
    private boolean hasMoreUserInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, getResources().getColor(R.color.bg_status_bar));
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        setup();
        loadBaseUserInfo();
        loadMoreUserInfo();
    }

    private void loadBaseUserInfo() {
        XMPPApi.fetchContactInfo(mContactInfo.getBareJid(), new OnFetchContactListener() {
            @Override
            public void onFetchContactInfo(ContactInfo contactInfo) {
                Log.d("Update contact from xmpp api");
                mContactInfo.apply(contactInfo);
                fillViews(mContactInfo);
            }
        });
    }

    private void loadMoreUserInfo() {
        XMPPApi.fetchLoginUser(mContactInfo.getBareJid(), new OnFetchLoginUserListener() {
            @Override
            public void onFetchLoginUser(final LoginUser user) {
                hasMoreUserInfo = user.getVCardExtendObject() != null;
                UiUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (hasMoreUserInfo) {
                            mMoreUserInfo.setText(R.string.label_has_more_user_info);
                        } else {
                            mMoreUserInfo.setText(R.string.label_no_more_user_info);
                        }
                    }
                });

            }

        });
    }

    /**
     * 设置界面显示
     */
    private void setup() {
        mBusinessHandler = ViewUserHelper.getBusinessHandler(getIntent());
        mBusinessHandler.setupViews(findViewById(android.R.id.content));
        mContactInfo = mBusinessHandler.getContactInfo();
        if (mContactInfo == null) {
            ToastUtil.showToastLong("获取联系人信息失败");
            finish();
            return;
        }
        fillViews(mContactInfo);
    }


    private void fillViews(final ContactInfo contactInfo) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageManager.displayImage(contactInfo.getAvatar(), mAvatarView, AvatarHelper.getOptions());
                mNickNameView.setText(contactInfo.getNickName());
                String displayName = UserInfoDisplay.getDisplayUserName(contactInfo.getUserName());
                mImUidView.setText(ResourceUtil.getString(R.string.id_name_format, contactInfo.getImUid()));

                if (TextUtils.isEmpty(contactInfo.getRemark())) {
                    mAliasTv.setText(R.string.set_alias_and_tag);
                } else {
                    mAliasTv.setText(contactInfo.getRemark());
                }

                //mAuthView.setImageResource(GenderHelper.getIconRes(contactInfo.getGender()));
                int thisYear = Calendar.getInstance().get(Calendar.YEAR);
                mAgeTv.setText(String.valueOf(thisYear - contactInfo.getBirth()));
                mGenderTv.setText(GenderHelper.getName(contactInfo.getGender()));
                mAuthView.setImageResource(UserInfoDisplay.getAuthStatusIcon(contactInfo.getAuthStatus()));
            }
        });

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @OnClick(R.id.avatar)
    void onClickAvatar() {
        DisplayAvatarActivity.launch(this, mContactInfo.getAvatar());
    }

    @OnClick(R.id.title_btn_back)
    void onClickBack() {
        finish();
    }

    /**
     * 点击标题栏中的more
     */
    @OnClick(R.id.title_btn_more)
    void onClickMore(View view) {
        PopupMenu popupMenu = new PopupMenu(this);

        MenuInfo reportUser = new MenuInfo();
        reportUser.setTitle(getString(R.string.report_user));
        reportUser.setOnClickMenuListener(new OnClickMenuListener() {
            @Override
            public void onClick(MenuInfo menu, int index) {
                reportUser();
            }
        });
        popupMenu.addItem(reportUser);

        mBusinessHandler.onPrepareMoreMenu(this, popupMenu, mContactInfo);

        View v = (View) view.getParent();
        popupMenu.show(v);
    }


    /**
     * 点击添加联系人
     */
    @OnClick(R.id.btn_add_to_contact)
    void onClickAddToContact() {
        List<ContactInfo> contactInfos = ContactDBCache.getContactList();
        LoginUser loginUser = XMPPManager.getInstance().getRosterManager().getLoginUser();
        if (null != contactInfos && null != loginUser) {
            if (loginUser.getAuth() == ContactInfo.AUTH_STATE_NOT_SUBMIT
                    || loginUser.getAuth() == ContactInfo.AUTH_STATE_SUBMITED
                    || loginUser.getAuth() == ContactInfo.AUTH_STATE_DENIED) {
                if (contactInfos.size() >= ContactInfo.CONTACT_LIMIT_UNAUTH) {
                    ToastUtil.showToast(ResourceUtil.getString(R.string.label_new_contact_reach_top, ContactInfo.CONTACT_LIMIT_UNAUTH));
                    return;
                }
            } else if (loginUser.getAuth() == ContactInfo.AUTH_STATE_AUTHENTICATED) {
                if (loginUser.getGrade() == 0 && contactInfos.size() >= ContactInfo.CONTACT_LIMIT_AUTH) {
                    ToastUtil.showToast(ResourceUtil.getString(R.string.label_new_contact_reach_top, ContactInfo.CONTACT_LIMIT_AUTH));
                    return;
                } else if (loginUser.getGrade() == 1 && contactInfos.size() >= ContactInfo.CONTACT_LIMIT_AUTH_GRADE_1) {
                    ToastUtil.showToast(ResourceUtil.getString(R.string.label_new_contact_reach_top, ContactInfo.CONTACT_LIMIT_AUTH_GRADE_1));
                    return;
                } else if (loginUser.getGrade() == 2 && contactInfos.size() >= ContactInfo.CONTACT_LIMIT_AUTH_GRADE_2) {
                    ToastUtil.showToast(ResourceUtil.getString(R.string.label_new_contact_reach_top, ContactInfo.CONTACT_LIMIT_AUTH_GRADE_2));
                    return;
                } else if (loginUser.getGrade() == 3 && contactInfos.size() >= ContactInfo.CONTACT_LIMIT_AUTH_GRADE_3) {
                    ToastUtil.showToast(ResourceUtil.getString(R.string.label_new_contact_reach_top, ContactInfo.CONTACT_LIMIT_AUTH_GRADE_3));
                    return;
                } else if (loginUser.getGrade() == 4 && contactInfos.size() >= ContactInfo.CONTACT_LIMIT_AUTH_GRADE_4) {
                    ToastUtil.showToast(ResourceUtil.getString(R.string.label_new_contact_reach_top, ContactInfo.CONTACT_LIMIT_AUTH_GRADE_4));
                    return;
                } else if (loginUser.getGrade() == 5 && contactInfos.size() >= ContactInfo.CONTACT_LIMIT_AUTH_GRADE_5) {
                    ToastUtil.showToast(ResourceUtil.getString(R.string.label_new_contact_reach_top, ContactInfo.CONTACT_LIMIT_AUTH_GRADE_5));
                    return;
                }
            }
        }

        boolean result = XMPPManager.getInstance().getRosterManager().requestAddContactToFriend(mContactInfo);
        if (result) {
            ToastUtil.showToastLong("已发送");
        } else {
            ToastUtil.showToastLong("添加好友失败");
        }
    }

    /**
     * 同意添加为好友
     */
    @OnClick(R.id.btn_pass_validation)
    void onClickPassValidation() {
        XMPPManager.getInstance().getRosterManager().acceptFriend(mContactInfo);
        ViewUserHelper.setContactType(getIntent());
        // 重置界面显示
        setup();
        EventBus.getDefault().post(new ContactOnAddEvent(mContactInfo.getBareJid()));
    }

    /**
     * 点击发送消息
     */
    @OnClick(R.id.btn_chat)
    void onClickChat() {
        ChatActivity.openChat(this, mContactInfo);
    }

    /**
     * 举报用户
     */
    private void reportUser() {
        Intent intent = new Intent(this, ReportUserActivity.class);
        intent.putExtra(ViewUserHelper.KEY_USER_INFO, mBusinessHandler.getContactInfo().getBareJid());
        startActivity(intent);
    }

    @OnClick(R.id.more_user_info)
    void onClickMoreInfo() {
        if (hasMoreUserInfo) {
            ViewUserHelper.viewContactInfoMore(this, mContactInfo);
        }
    }

    @OnClick(R.id.setup_alias)
    void onClickSetupAlias() {
        final String originRemark = mContactInfo.getRemark();
        final EditText aliasInput = new EditText(this);
        aliasInput.setText(originRemark);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置备注").setView(aliasInput)
                .setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                String newRemark = aliasInput.getText().toString();
                if (newRemark.equals(originRemark)) {
                    return;
                }
                XMPPManager.getInstance().getRosterManager().changeNickName(mContactInfo.getBareJid(), newRemark);
                mContactInfo.setRemark(newRemark);
            }
        });
        builder.show();
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
}
