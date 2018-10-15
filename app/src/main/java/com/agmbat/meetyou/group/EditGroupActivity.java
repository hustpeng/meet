package com.agmbat.meetyou.group;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.agmbat.android.SysResources;
import com.agmbat.android.image.ImageManager;
import com.agmbat.android.utils.ToastUtil;
import com.agmbat.imagepicker.ImagePickerHelper;
import com.agmbat.imagepicker.OnPickImageListener;
import com.agmbat.imagepicker.PickerOption;
import com.agmbat.imagepicker.bean.ImageItem;
import com.agmbat.imagepicker.view.CropImageView;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.group.GroupFormReply;
import com.agmbat.imsdk.group.QueryGroupFormIQ;
import com.agmbat.imsdk.group.UpdateGroupIQ;
import com.agmbat.imsdk.group.UpdateGroupReply;
import com.agmbat.imsdk.remotefile.FileApiResult;
import com.agmbat.imsdk.remotefile.OnFileUploadListener;
import com.agmbat.imsdk.remotefile.RemoteFileManager;
import com.agmbat.imsdk.search.group.GroupCategory;
import com.agmbat.isdialog.ISLoadingDialog;
import com.agmbat.log.Log;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.discovery.search.TagSelectedView;
import com.agmbat.meetyou.helper.AvatarHelper;
import com.agmbat.meetyou.util.CircleDrawable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditGroupActivity extends Activity {

    private static final String EXTRA_GROUP_JID = "group_jid";

    @BindView(R.id.avatar)
    ImageView mAvatarView;
    @BindView(R.id.name_input)
    EditText mGroupNameTv;
    @BindView(R.id.headline_input)
    EditText mHeadlineTv;
    @BindView(R.id.description_input)
    EditText mDescriptionTv;
    @BindView(R.id.verify_check)
    CheckBox mVerifyCheckBox;
    @BindView(R.id.tag_selected_view)
    TagSelectedView mCategorysView;

    private String mGroupJid;
    private String mAvatarPath;
    private String mAvatarUrl;
    private PacketListener mGroupFormListener = new PacketListener() {
        @Override
        public void processPacket(Packet packet) {
            if (packet instanceof GroupFormReply) {
                GroupFormReply groupFormReply = (GroupFormReply) packet;
                GroupForm groupForm = new GroupForm();
                groupForm.setName(groupFormReply.getName());
                groupForm.setHeadline(groupFormReply.getHeadline());
                groupForm.setDescription(groupFormReply.getDescription());
                groupForm.setNeedVerify(groupFormReply.isNeedVerify());
                groupForm.setAvatar(groupFormReply.getAvatar());
                groupForm.setCategoryId(groupFormReply.getCategoryId());
                groupForm.setCategories(groupFormReply.getCategories());
                EventBus.getDefault().post(groupForm);
            }
        }
    };
    private int mCategoryId;
    private ISLoadingDialog mUpdateProgressDialog;
    private PacketListener mUpdateGroupListener = new PacketListener() {
        @Override
        public void processPacket(Packet packet) {
            if (packet instanceof UpdateGroupReply) {
                UpdateGroupReply updateGroupReply = (UpdateGroupReply) packet;
                dismissUpdateProgressDialog();
                if (updateGroupReply.isSuccess()) {
                    ToastUtil.showToast("修改成功");
                    EditGroupEvent editGroupEvent = new EditGroupEvent(mGroupJid);
                    editGroupEvent.setAvatar(mAvatarUrl);
                    editGroupEvent.setGroupName(mGroupNameTv.getText().toString());
                    editGroupEvent.setHeadline(mHeadlineTv.getText().toString());
                    editGroupEvent.setDescription(mDescriptionTv.getText().toString());
                    editGroupEvent.setNeedVerify(mVerifyCheckBox.isChecked());
                    editGroupEvent.setCategoryId(mCategoryId);
                    EventBus.getDefault().post(editGroupEvent);
                    finish();
                } else {
                    ToastUtil.showToast("修改失败");
                }
            }
        }
    };

    public static void launch(Context context, String groupJid) {
        Intent intent = new Intent(context, EditGroupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_GROUP_JID, groupJid);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGroupJid = getIntent().getStringExtra(EXTRA_GROUP_JID);
        setContentView(R.layout.activity_edit_group);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        XMPPManager.getInstance().getXmppConnection().addPacketListener(mGroupFormListener, new PacketTypeFilter(GroupFormReply.class));
        XMPPManager.getInstance().getXmppConnection().addPacketListener(mUpdateGroupListener, new PacketTypeFilter(UpdateGroupReply.class));
        XMPPManager.getInstance().getXmppConnection().sendPacket(new QueryGroupFormIQ(mGroupJid));

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GroupForm groupForm) {
        fillGroupForm(groupForm);
    }

    private void fillGroupForm(GroupForm groupForm) {
        mAvatarUrl = groupForm.getAvatar();
        Log.d("Fill the group to form:" + groupForm.toString());
        if (!TextUtils.isEmpty(mAvatarUrl)) {
            ImageManager.displayImage(mAvatarUrl, mAvatarView, AvatarHelper.getGroupOptions());
        }
        mGroupNameTv.setText(groupForm.getName());
        mHeadlineTv.setText(groupForm.getHeadline());
        mDescriptionTv.setText(groupForm.getDescription());
        mVerifyCheckBox.setChecked(groupForm.isNeedVerify());

        final List<GroupCategory> categories = groupForm.getCategories();
        List<String> categoryTags = new ArrayList<>();
        String selectedTag = "";
        for (int i = 0; i < categories.size(); i++) {
            GroupCategory current = categories.get(i);
            categoryTags.add(current.getName());
            if (current.getId() == groupForm.getCategoryId()) {
                selectedTag = current.getName();
            }
        }
        mCategorysView.setTagList(categoryTags);
        mCategorysView.setOnSelectedListener(new TagSelectedView.OnSelectedListener() {
            @Override
            public void onSelected(int index, String tag) {
                mCategoryId = categories.get(index).getId();
            }
        });
        mCategorysView.setSelectedTag(selectedTag);
        mCategorysView.setEnabled(false);
    }

    @OnClick(R.id.title_btn_back)
    void onClickBack() {
        finish();
    }

    @OnClick(R.id.title_btn_next)
    void onClickSave() {
        showUpdateProgressDialog();
        if (!TextUtils.isEmpty(mAvatarPath)) {
            uploadGroupAvatar(mAvatarPath, mGroupJid);
        } else {
            sendUpdatePacket(mAvatarUrl);
        }
    }

    private void sendUpdatePacket(String avatarUrl) {
        UpdateGroupIQ updateGroupIQ = new UpdateGroupIQ(mGroupJid);
        updateGroupIQ.setGroupName(mGroupNameTv.getText().toString());
        updateGroupIQ.setHeadline(mHeadlineTv.getText().toString());
        updateGroupIQ.setDescription(mDescriptionTv.getText().toString());
        updateGroupIQ.setCategoryId(mCategoryId);
        updateGroupIQ.setNeedVerify(mVerifyCheckBox.isChecked());
        updateGroupIQ.setAvatar(avatarUrl);
        XMPPManager.getInstance().getXmppConnection().sendPacket(updateGroupIQ);
    }

    /**
     * 点击头像, 添加群头像
     */
    @OnClick(R.id.avatar)
    void onClickAvatar() {
        final PickerOption.CropParams params = new PickerOption.CropParams();
        params.setStyle(CropImageView.Style.RECTANGLE); //裁剪框的形状
        params.setFocusWidth(800); //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        params.setFocusHeight(800); //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        params.setOutPutX((int) SysResources.dipToPixel(150));//保存文件的宽度。单位像素
        params.setOutPutY((int) SysResources.dipToPixel(150));//保存文件的高度。单位像素 }

        PickerOption option = new PickerOption();
        option.setShowCamera(false);
        option.setCropParams(params);

        ImagePickerHelper.pickImage(this, option, new OnPickImageListener() {
            @Override
            public void onPickImage(ImageItem imageItem) {
                mAvatarPath = imageItem.path;
                //String uri = Scheme.wrapUri("file", imageItem.path);
                //ImageManager.displayImage(uri, mAvatarView, AvatarHelper.getGroupOptions());
                Bitmap bitmap = BitmapFactory.decodeFile(imageItem.path);
                mAvatarView.setImageDrawable(new CircleDrawable(bitmap));
                //ImageUtil.loadCircleImage(getBaseContext(), mAvatarView, "",  );
            }
        });
    }

    /**
     * 上传群头像
     *
     * @param path
     */
    private void uploadGroupAvatar(String path, String groupJid) {
        RemoteFileManager.uploadAvatarFile(path, groupJid, new OnFileUploadListener() {

            @Override
            public void onUpload(FileApiResult apiResult) {
                Log.d("Upload group avatar result: " + apiResult.mResult + "url: " + apiResult.url + ", errorMsg: " + apiResult.mErrorMsg);
                if (apiResult.mResult) {
                    mAvatarUrl = apiResult.url;
                }
                sendUpdatePacket(apiResult.url);
            }
        });
    }

    private void showUpdateProgressDialog() {
        if (null == mUpdateProgressDialog || !mUpdateProgressDialog.isShowing()) {
            // 添加loading框
            mUpdateProgressDialog = new ISLoadingDialog(this);
            mUpdateProgressDialog.setMessage("正在保存...");
            mUpdateProgressDialog.setCancelable(true);
            mUpdateProgressDialog.show();
        }
    }

    private void dismissUpdateProgressDialog() {
        if (null != mUpdateProgressDialog && mUpdateProgressDialog.isShowing()) {
            mUpdateProgressDialog.dismiss();
            mUpdateProgressDialog = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        XMPPManager.getInstance().getXmppConnection().removePacketListener(mGroupFormListener);
        XMPPManager.getInstance().getXmppConnection().removePacketListener(mUpdateGroupListener);
    }
}
