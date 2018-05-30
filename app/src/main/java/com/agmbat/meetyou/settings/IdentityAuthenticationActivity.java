package com.agmbat.meetyou.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;

import com.agmbat.android.image.ImageManager;
import com.agmbat.android.utils.ToastUtil;
import com.agmbat.android.utils.WindowUtils;
import com.agmbat.file.FileUtils;
import com.agmbat.imagepicker.ImagePicker;
import com.agmbat.imagepicker.bean.ImageItem;
import com.agmbat.imagepicker.loader.UILImageLoader;
import com.agmbat.imagepicker.ui.ImageGridActivity;
import com.agmbat.imsdk.Identity.IdentityApi;
import com.agmbat.imsdk.Identity.IdentityManager;
import com.agmbat.imsdk.Identity.OnIdentityListener;
import com.agmbat.imsdk.api.ApiResult;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.remotefile.FileApi;
import com.agmbat.imsdk.remotefile.FileApiResult;
import com.agmbat.imsdk.user.LoginUser;
import com.agmbat.isdialog.ISLoadingDialog;
import com.agmbat.meetyou.R;
import com.nostra13.universalimageloader.core.download.Scheme;

import java.io.File;
import java.util.ArrayList;

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

    /**
     * 身份证背面照片
     */
    @BindView(R.id.identity_image_back)
    ImageView mBackImageView;

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


    private ISLoadingDialog mISLoadingDialog;

    private String mFrontPath;
    private String mBackPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, getResources().getColor(R.color.bg_status_bar));
        setContentView(R.layout.activity_identity_autentication);
        ButterKnife.bind(this);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_TAKE_PICTURE) {
            if (data != null) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null) {
                    if (images.size() == 1) {
                        onTakePhoto(images.get(0).path);
                    }
                }
            }
        }
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
        if (TextUtils.isEmpty(mFrontPath) || TextUtils.isEmpty(mBackPath)) {
            ToastUtil.showToast("请拍摄两张身份认证照片");
            return;
        }
        final String name = mInputNameView.getText().toString();
        final String identity = mInputIdentityView.getText().toString();
        // 上传照片
        showLoadingDialog();
        IdentityManager.auth(name, identity, mFrontPath, mBackPath, new OnIdentityListener() {
            @Override
            public void onIdentity(ApiResult apiResult) {
                hideLoadingDialog();
                ToastUtil.showToast(apiResult.mErrorMsg);
            }
        });
    }

    private int type = 0;

    /**
     * 点击拍身份证前面照片
     */
    @OnClick(R.id.btn_identity_front)
    void onClickTakeFront() {
        type = 0;
        takePicture();
    }

    /**
     * 点击拍身份证背面照片
     */
    @OnClick(R.id.btn_identity_back)
    void onClickTakeBack() {
        type = 1;
        takePicture();
    }


    private void onTakePhoto(String path) {
        if (type == 0) {
            mFrontPath = path;
            ImageManager.displayImage(Scheme.wrapUri("file", path), mFontImageView);
        } else {
            mBackPath = path;
            ImageManager.displayImage(Scheme.wrapUri("file", path), mBackImageView);
        }
    }

    private static final int REQUEST_CODE_TAKE_PICTURE = 121;

    /**
     * 拍照
     */
    private void takePicture() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new UILImageLoader());
        imagePicker.setCrop(false);
        Intent intent = new Intent(this, ImageGridActivity.class);
        intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
        startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
    }

    /**
     * 显示loading框
     */
    private void showLoadingDialog() {
        if (mISLoadingDialog == null) {
            mISLoadingDialog = new ISLoadingDialog(this);
            mISLoadingDialog.setMessage("正在上传...");
            mISLoadingDialog.setCancelable(false);
        }
        mISLoadingDialog.show();
    }

    /**
     * 隐藏loading框
     */
    private void hideLoadingDialog() {
        if (mISLoadingDialog != null) {
            mISLoadingDialog.dismiss();
        }
    }


}
