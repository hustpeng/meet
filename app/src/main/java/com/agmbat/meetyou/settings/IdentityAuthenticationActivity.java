package com.agmbat.meetyou.settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.agmbat.android.image.ImageManager;
import com.agmbat.android.utils.ToastUtil;
import com.agmbat.android.utils.WindowUtils;
import com.agmbat.imagepicker.ImagePickerHelper;
import com.agmbat.imagepicker.OnPickImageListener;
import com.agmbat.imagepicker.bean.ImageItem;
import com.agmbat.imsdk.Identity.Auth;
import com.agmbat.imsdk.Identity.AuthStatusResult;
import com.agmbat.imsdk.Identity.IdentityManager;
import com.agmbat.imsdk.Identity.OnIdentityListener;
import com.agmbat.imsdk.Identity.OnLoadAuthStatusListener;
import com.agmbat.imsdk.api.ApiResult;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.user.LoginUser;
import com.agmbat.isdialog.ISLoadingDialog;
import com.agmbat.meetyou.R;
import com.nostra13.universalimageloader.core.download.Scheme;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 身份认证界面
 */
public class IdentityAuthenticationActivity extends Activity {

    /**
     * 身份证前面照片
     */
    @BindView(R.id.identity_image_front)
    ImageView mFontImageView;

    @BindView(R.id.btn_identity_front)
    View mFontImageBtn;

    /**
     * 身份证背面照片
     */
    @BindView(R.id.identity_image_back)
    ImageView mBackImageView;

    @BindView(R.id.btn_identity_back)
    View mBackImageBtn;

    /**
     * 姓名编辑框
     */
    @BindView(R.id.input_name)
    EditText mInputNameView;

    /**
     * 身份证号编辑框
     */
    @BindView(R.id.input_identity)
    EditText mInputIdentityView;

    @BindView(R.id.text)
    TextView mStatusView;

    @BindView(R.id.identity_authentication_layout)
    View mIdentityAuthenticationLayout;

    private ISLoadingDialog mISLoadingDialog;

    /**
     * 第一张图片路径
     */
    private String mFrontPath;

    /**
     * 第一张url, 存在从服务器下载后未修改图片再次上传的情况
     */
    private String mFrontUrl;


    /**
     * 第二张图片路径
     */
    private String mBackPath;

    /**
     * 第二张url
     */
    private String mBackUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, getResources().getColor(R.color.bg_status_bar));
        setContentView(R.layout.activity_identity_autentication);
        ButterKnife.bind(this);
        initContentView();
        showLoadingDialog("正在获取身份验证状态...");
        IdentityManager.authStatus(new OnLoadAuthStatusListener() {
            @Override
            public void onLoadAuthStatus(AuthStatusResult result) {
                hideLoadingDialog();
                if (!result.mResult) {
                    ToastUtil.showToast("获取身份状态失败");
                    finish();
                    return;
                }
                LoginUser loginUser = XMPPManager.getInstance().getRosterManager().getLoginUser();
                if (loginUser.getVCardExtendObject() != null) {
                    ToastUtil.showToast(result.mErrorMsg);
                    mStatusView.setText("当前身份认证状态:" + result.mErrorMsg);
                } else {
                    mStatusView.setText("请先完善个人信息，再进行身份验证");
                }
                if (result.mAuth.hasNeedAuth()) {
                    mIdentityAuthenticationLayout.setVisibility(View.VISIBLE);
                }

                // 如果已上传过则显示相关信息
                if (result.mAuth.mStatus == Auth.STATUS_PASS || result.mAuth.mStatus == Auth.STATUS_PENDING_TRIAL) {
                    mInputNameView.setText(result.mAuth.mName);
                    mInputIdentityView.setText(result.mAuth.mIdentity);
                    mFrontUrl = result.mAuth.mPhotoFront;
                    mBackUrl = result.mAuth.mPhotoBack;
                    ImageManager.displayImage(result.mAuth.mPhotoFront, mFontImageView);
                    ImageManager.displayImage(result.mAuth.mPhotoBack, mBackImageView);
                }
            }
        });
    }


    private void initContentView() {
        mIdentityAuthenticationLayout.setVisibility(View.GONE);
        LoginUser loginUser = XMPPManager.getInstance().getRosterManager().getLoginUser();
        boolean isEnable = loginUser.getVCardExtendObject() != null;
        mInputNameView.setEnabled(isEnable);
        mInputIdentityView.setEnabled(isEnable);
        mFontImageBtn.setEnabled(isEnable);
        mBackImageBtn.setEnabled(isEnable);
        if (!isEnable) {
            ToastUtil.showToast("请先完善个人信息，再进行身份验证");
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    /**
     * 点击返回键
     */
    @OnClick(R.id.title_btn_back)
    void onClickBack() {
        finish();
    }

    /**
     * 点击保存
     */
    @OnClick(R.id.btn_save)
    void onClickSave() {
        LoginUser loginUser = XMPPManager.getInstance().getRosterManager().getLoginUser();
        if (loginUser.getVCardExtendObject() == null) {
            ToastUtil.showToast("请先完善个人信息，再进行身份验证");
            return;
        }
        final String name = mInputNameView.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            ToastUtil.showToast("请输入姓名!");
            return;
        }
        final String identity = mInputIdentityView.getText().toString();
        if (TextUtils.isEmpty(identity)) {
            ToastUtil.showToast("请输入身份证号码!");
            return;
        }

        if (TextUtils.isEmpty(mFrontPath) && TextUtils.isEmpty(mFrontUrl)) {
            ToastUtil.showToast("请拍摄两张身份认证照片");
            return;
        }

        if (TextUtils.isEmpty(mBackPath) && TextUtils.isEmpty(mBackUrl)) {
            ToastUtil.showToast("请拍摄两张身份认证照片");
            return;
        }

//        if(loginUser.getHeight() == 0){
//            ToastUtil.showToast("您还未填写身高，请先完善个人信息");
//            return;
//        }
//        if(TextUtils.isEmpty(loginUser.getWorkarea())){
//            ToastUtil.showToast("您还未填写工作地区，请先完善个人信息");
//            return;
//        }
//        if(loginUser.getWeight() == 0){
//            ToastUtil.showToast("您还未填写体重，请先完善个人信息");
//            return;
//        }
//        if(TextUtils.isEmpty(loginUser.getBirthplace())){
//            ToastUtil.showToast("您还未填写籍贯，请先完善个人信息");
//            return;
//        }
//        if(TextUtils.isEmpty(loginUser.getResidence())){
//            ToastUtil.showToast("您还未填写户口所在地，请先完善个人信息");
//            return;
//        }
//        if(TextUtils.isEmpty(loginUser.getIndustry())){
//            ToastUtil.showToast("您还未填写行业，请先完善个人信息");
//            return;
//        }
//        if(TextUtils.isEmpty(loginUser.getCareer())){
//            ToastUtil.showToast("您还未填写职业，请先完善个人信息");
//            return;
//        }
//        if(TextUtils.isEmpty(loginUser.getHobby())){
//            ToastUtil.showToast("您还未填写兴趣爱好，请先完善个人信息");
//            return;
//        }
//        if(TextUtils.isEmpty(loginUser.getIntroduce())){
//            ToastUtil.showToast("您还未填写个人简介，请先完善个人信息");
//            return;
//        }

        // 上传照片
        showLoadingDialog("正在上传认证资料...");
        IdentityManager.auth(name, identity, mFrontPath, mBackPath, mFrontUrl, mBackUrl, new OnIdentityListener() {
            @Override
            public void onIdentity(ApiResult apiResult) {
                hideLoadingDialog();
                ToastUtil.showToast(apiResult.mErrorMsg);
            }
        });
    }

    /**
     * 点击拍身份证前面照片
     */
    @OnClick(R.id.btn_identity_front)
    void onClickTakeFront() {
        ImagePickerHelper.takePicture(this, new OnPickImageListener() {
            @Override
            public void onPickImage(ImageItem imageItem) {
                mFrontPath = imageItem.path;
                ImageManager.displayImage(Scheme.wrapUri("file", mFrontPath), mFontImageView);
            }
        });
    }

    /**
     * 点击拍身份证背面照片
     */
    @OnClick(R.id.btn_identity_back)
    void onClickTakeBack() {
        ImagePickerHelper.takePicture(this, new OnPickImageListener() {
            @Override
            public void onPickImage(ImageItem imageItem) {
                mBackPath = imageItem.path;
                ImageManager.displayImage(Scheme.wrapUri("file", mBackPath), mBackImageView);
            }
        });
    }

    @OnClick(R.id.remove_front)
    void onClickRemoveFront() {
        mFrontPath = null;
        mFrontUrl = null;
        mFontImageView.setImageResource(R.drawable.auth_front);
    }

    @OnClick(R.id.remove_back)
    void onClickRemoveBack() {
        mBackPath = null;
        mBackUrl = null;
        mBackImageView.setImageResource(R.drawable.auth_back);
    }

    /**
     * 显示loading框
     */
    private void showLoadingDialog(String msg) {
        if (mISLoadingDialog == null) {
            mISLoadingDialog = new ISLoadingDialog(this);
            mISLoadingDialog.setCancelable(false);
        }
        mISLoadingDialog.setMessage(msg);
        try {
            mISLoadingDialog.show();
        }catch (Exception e){
        }
    }

    /**
     * 隐藏loading框
     */
    private void hideLoadingDialog() {
        if (mISLoadingDialog != null && mISLoadingDialog.isShowing()) {
            try {
                mISLoadingDialog.dismiss();
            }catch (Exception e){
            }
        }
    }

}
