package com.agmbat.meetyou.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;
import android.widget.ImageView;

import com.agmbat.android.image.ImageManager;
import com.agmbat.android.utils.ToastUtil;
import com.agmbat.android.utils.WindowUtils;
import com.agmbat.imagepicker.ImagePicker;
import com.agmbat.imagepicker.ImagePickerHelper;
import com.agmbat.imagepicker.OnPickImageListener;
import com.agmbat.imagepicker.PickerOption;
import com.agmbat.imagepicker.bean.ImageItem;
import com.agmbat.imagepicker.loader.UILImageLoader;
import com.agmbat.imagepicker.ui.ImageGridActivity;
import com.agmbat.imsdk.api.ApiResult;
import com.agmbat.imsdk.feedback.FeedbackManager;
import com.agmbat.imsdk.feedback.OnFeedbackListener;
import com.agmbat.isdialog.ISLoadingDialog;
import com.agmbat.meetyou.R;
import com.nostra13.universalimageloader.core.download.Scheme;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 用户反馈界面
 */
public class FeedbackActivity extends Activity {

    /**
     * 显示用户反馈图片
     */
    @BindView(R.id.btn_add_image)
    ImageView mImageView;

    /**
     * 姓名编辑框
     */
    @BindView(R.id.input_text)
    EditText mInputView;

    /**
     * loading对话框
     */
    private ISLoadingDialog mISLoadingDialog;

    /**
     * 上传的图片路径
     */
    private String mPhotoPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, getResources().getColor(R.color.bg_status_bar));
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);
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
        final String content = mInputView.getText().toString().trim();
        if (content.length() < 4) {
            ToastUtil.showToast(getString(R.string.feedback_tips));
            return;
        }
        // 上传照片
        showLoadingDialog();
        FeedbackManager.feedback(content, mPhotoPath, new OnFeedbackListener() {
            @Override
            public void onFeedback(ApiResult result) {
                hideLoadingDialog();
                ToastUtil.showToast(result.mErrorMsg);
                if (result.mResult) {
                    finish();
                }
            }
        });
    }

    /**
     * 点击添加反馈图片
     */
    @OnClick(R.id.btn_add_image)
    void onClickAddImage() {
        takePicture();
    }

    @OnClick(R.id.remove)
    void onClickClear() {
        mPhotoPath = null;
        mImageView.setImageResource(R.drawable.selector_image_add);
    }

    /**
     * 拍照
     */
    private void takePicture() {
        PickerOption option = new PickerOption();
        option.setShowCamera(true);
        ImagePickerHelper.pickImage(this, option, new OnPickImageListener() {
            @Override
            public void onPickImage(ImageItem imageItem) {
                mPhotoPath = imageItem.path;
                ImageManager.displayImage(Scheme.wrapUri("file", imageItem.path), mImageView);
            }
        });
    }

    /**
     * 显示loading框
     */
    private void showLoadingDialog() {
        if (mISLoadingDialog == null) {
            mISLoadingDialog = new ISLoadingDialog(this);
            mISLoadingDialog.setMessage(getString(R.string.feedback_loading));
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
