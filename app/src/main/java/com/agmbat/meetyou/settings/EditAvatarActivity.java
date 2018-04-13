package com.agmbat.meetyou.settings;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;

import com.agmbat.android.image.ImageManager;
import com.agmbat.android.utils.ToastUtil;
import com.agmbat.android.utils.WindowUtils;
import com.agmbat.file.FileUtils;
import com.agmbat.imagepicker.ImagePicker;
import com.agmbat.imagepicker.bean.ImageItem;
import com.agmbat.imagepicker.loader.UILImageLoader;
import com.agmbat.imagepicker.ui.ImageGridActivity;
import com.agmbat.imagepicker.view.CropImageView;
import com.agmbat.imsdk.IM;
import com.agmbat.imsdk.api.ApiResult;
import com.agmbat.imsdk.remotefile.RemoteFileManager;
import com.agmbat.isalertview.AlertView;
import com.agmbat.isalertview.OnItemClickListener;
import com.agmbat.meetyou.R;
import com.agmbat.photoview.PhotoView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smackx.vcard.VCardObject;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 编辑头像界面
 */
public class EditAvatarActivity extends Activity {

    /**
     * 拍照
     */
    private static final int REQUEST_CODE_TAKE_PICTURE = 100;

    @BindView(R.id.image)
    PhotoView mPhotoView;

    /**
     * 当前用户信息
     */
    private VCardObject mVCardObject;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, 0xff232325);
        setContentView(R.layout.activity_edit_avatar);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        IM.get().fetchMyVCard();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(VCardObject vCardObject) {
        mVCardObject = vCardObject;
        ImageManager.displayImage(vCardObject.getAvatar(), mPhotoView);
    }

    /**
     * 点击返回键
     */
    @OnClick(R.id.title_btn_back)
    void onClickBack() {
        finish();
    }

    /**
     * 点击menu
     */
    @OnClick(R.id.title_menu_more)
    void onClickMenuMore() {
        new AlertView(null, null, "取消", null,
                new String[]{"拍照", "从手机相册选择", "保存图片"},
                this, AlertView.Style.ActionSheet, new OnItemClickListener() {
            @Override
            public void onItemClick(AlertView view, int position) {
                if (position == 0) {
                    takePicture();
                } else if (position == 1) {
                    selectPicture();
                } else if (position == 2) {
                    savePicture();
                }
            }
        }).setCancelable(true).show();
    }


    /**
     * 拍照
     */
    private void takePicture() {
        ImagePicker.getInstance().setImageLoader(new UILImageLoader());
        Intent intent = new Intent(this, ImageGridActivity.class);
        intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
        startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
    }

    /**
     * 选择图片
     */
    private void selectPicture() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new UILImageLoader());
        imagePicker.setShowCamera(false); //显示拍照按钮
        imagePicker.setCrop(true); //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
        imagePicker.setSelectLimit(1); //选中数量限制
        imagePicker.setMultiMode(false); // 设置为单选
        imagePicker.setStyle(CropImageView.Style.RECTANGLE); //裁剪框的形状
        imagePicker.setFocusWidth(800); //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800); //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素 }
        Intent intent = new Intent(this, ImageGridActivity.class);
        startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
    }

    /**
     * 保存图片
     */
    private void savePicture() {
        File imageFile = ImageManager.getDiskCacheFile(mVCardObject.getAvatar());
        File headFile = new File(Environment.getExternalStorageDirectory(), "head_" + System.currentTimeMillis() + ".jpg");
        FileUtils.copyFileNio(imageFile, headFile);
        // TODO 发送广播让系统将文件扫描到系统数据库中
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_TAKE_PICTURE) {
            if (data != null) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null) {
                    String path = images.get(0).path;
                    uploadAvatarFile(path);
                }
            }
        }
    }

    /**
     * 上传图片文件
     *
     * @param path
     */
    private void uploadAvatarFile(String path) {
        // TODO 添加loading框
        RemoteFileManager.uploadAvatarFile(path, new RemoteFileManager.OnFileUploadListener() {

            @Override
            public void onUpload(ApiResult<String> apiResult) {
                ToastUtil.showToastLong(apiResult.mErrorMsg);
            }
        });
    }
}
