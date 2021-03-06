package com.agmbat.meetyou.edituserinfo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;

import com.agmbat.android.image.ImageManager;
import com.agmbat.android.utils.ToastUtil;
import com.agmbat.android.utils.WindowUtils;
import com.agmbat.file.FileUtils;
import com.agmbat.imagepicker.ImagePickerHelper;
import com.agmbat.imagepicker.OnPickImageListener;
import com.agmbat.imagepicker.PickerOption;
import com.agmbat.imagepicker.bean.ImageItem;
import com.agmbat.imagepicker.view.CropImageView;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.imevent.LoginUserUpdateEvent;
import com.agmbat.imsdk.remotefile.FileApiResult;
import com.agmbat.imsdk.remotefile.OnFileUploadListener;
import com.agmbat.imsdk.remotefile.RemoteFileManager;
import com.agmbat.imsdk.user.LoginUser;
import com.agmbat.isdialog.ISActionSheetDialog;
import com.agmbat.isdialog.ISLoadingDialog;
import com.agmbat.log.Debug;
import com.agmbat.meetyou.R;
import com.agmbat.menu.MenuInfo;
import com.agmbat.menu.OnClickMenuListener;
import com.agmbat.photoview.PhotoView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 编辑头像界面
 */
public class EditAvatarActivity extends Activity {

    @BindView(R.id.avatar)
    PhotoView mPhotoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, getResources().getColor(R.color.bg_status_bar));
        setContentView(R.layout.activity_edit_avatar);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        LoginUser user = XMPPManager.getInstance().getRosterManager().getLoginUser();
        update(user);
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

    /**
     * 点击menu
     */
    @OnClick(R.id.title_menu_more)
    void onClickMenuMore() {
        ISActionSheetDialog dialog = new ISActionSheetDialog(this);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.useNegativeButton();
        dialog.addItem("拍照", new OnClickMenuListener() {
            @Override
            public void onClick(MenuInfo menu, int index) {
                takePicture();
            }

        });
        dialog.addItem("从手机相册选择", new OnClickMenuListener() {
            @Override
            public void onClick(MenuInfo menu, int which) {
                selectPicture();
            }
        });
        dialog.addItem("保存图片", new OnClickMenuListener() {
            @Override
            public void onClick(MenuInfo menu, int which) {
                savePicture();
            }
        });
        dialog.show();
    }


    /**
     * 拍照
     */
    private void takePicture() {
        ImagePickerHelper.takePicture(this, new OnPickImageListener() {
            @Override
            public void onPickImage(ImageItem imageItem) {
                String path = imageItem.path;
                uploadAvatarFile(path);
            }
        });
    }

    /**
     * 选择图片
     */
    private void selectPicture() {
        PickerOption.CropParams params = new PickerOption.CropParams();
        params.setStyle(CropImageView.Style.RECTANGLE); //裁剪框的形状
        params.setFocusWidth(800); //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        params.setFocusHeight(800); //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        params.setOutPutX(200);//保存文件的宽度。单位像素
        params.setOutPutY(200);//保存文件的高度。单位像素 }

        PickerOption option = new PickerOption();
        option.setShowCamera(false);
        option.setCropParams(params);

        ImagePickerHelper.pickImage(this, option, new OnPickImageListener() {
            @Override
            public void onPickImage(ImageItem imageItem) {
                String path = imageItem.path;
                uploadAvatarFile(path);
            }
        });
    }

    /**
     * 保存图片
     */
    private void savePicture() {
        LoginUser user = XMPPManager.getInstance().getRosterManager().getLoginUser();
        File imageFile = ImageManager.getDiskCacheFile(user.getAvatar());
        File headFile = new File(Environment.getExternalStorageDirectory(), "head_" + System.currentTimeMillis() + ".jpg");
        FileUtils.copyFileNio(imageFile, headFile);
        // TODO 发送广播让系统将文件扫描到系统数据库中
    }


    /**
     * 上传图片文件
     *
     * @param path
     */
    private void uploadAvatarFile(String path) {
        // 添加loading框
        final ISLoadingDialog dialog = new ISLoadingDialog(this);
        dialog.setMessage("正在上传头像...");
        dialog.setCancelable(false);
        dialog.show();
        RemoteFileManager.uploadAvatarFile(path, "", new OnFileUploadListener() {

            @Override
            public void onUpload(FileApiResult apiResult) {
                ToastUtil.showToastLong(apiResult.mErrorMsg);
                dialog.dismiss();
                if (apiResult.mResult) {
                    LoginUser user = XMPPManager.getInstance().getRosterManager().getLoginUser();
                    user.setAvatar(apiResult.url);
                    XMPPManager.getInstance().getRosterManager().saveLoginUser(user);
                }

            }
        });
    }

    /**
     * 更新用户信息显示
     *
     * @param user
     */
    private void update(LoginUser user) {
        if (user == null || !user.isValid()) {
            Debug.printStackTrace();
            return;
        }
        ImageManager.displayImage(user.getAvatar(), mPhotoView);
    }
}
