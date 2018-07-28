package com.agmbat.meetyou.group;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.agmbat.android.image.ImageManager;
import com.agmbat.imagepicker.ImagePickerHelper;
import com.agmbat.imagepicker.OnPickImageListener;
import com.agmbat.imagepicker.PickerOption;
import com.agmbat.imagepicker.bean.ImageItem;
import com.agmbat.imagepicker.view.CropImageView;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.group.CreateGroupResultIQ;
import com.agmbat.imsdk.group.GroupFormReply;
import com.agmbat.imsdk.group.QueryGroupFormIQ;
import com.agmbat.imsdk.search.group.GroupCategory;
import com.agmbat.log.Log;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.discovery.search.TagSelectedView;
import com.agmbat.meetyou.helper.AvatarHelper;
import com.agmbat.meetyou.util.CircleDrawable;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

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
    TextView mGroupNameTv;
    @BindView(R.id.headline_input)
    TextView mHeadlineTv;
    @BindView(R.id.description_input)
    TextView mDescriptionTv;
    @BindView(R.id.verify_check)
    CheckBox mVerifyCheckBox;
    @BindView(R.id.tag_selected_view)
    TagSelectedView mCategorysView;

    private String mGroupJid;

    public static void launch(Context context, String groupJid){
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
        XMPPManager.getInstance().getXmppConnection().sendPacket(new QueryGroupFormIQ(mGroupJid));
    }

    private PacketListener mGroupFormListener = new PacketListener() {
        @Override
        public void processPacket(Packet packet) {
            if(packet instanceof GroupFormReply){
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GroupForm groupForm) {
        fillGroupForm(groupForm);
    }


    private void fillGroupForm(GroupForm groupForm){
        Log.d("Fill the group to form:" + groupForm.toString());
        if(!TextUtils.isEmpty(groupForm.getAvatar())){
            ImageManager.displayImage(groupForm.getAvatar(), mAvatarView, AvatarHelper.getGroupOptions());
        }
        mGroupNameTv.setText(groupForm.getName());
        mHeadlineTv.setText(groupForm.getHeadline());
        mDescriptionTv.setText(groupForm.getDescription());
        mVerifyCheckBox.setChecked(groupForm.isNeedVerify());

        List<GroupCategory> categories = groupForm.getCategories();
        List<String> categoryTags = new ArrayList<>();
        String selectedTag = "";
        for (int i = 0; i < categories.size(); i++) {
            GroupCategory current = categories.get(i);
            categoryTags.add(current.getName());
            if(current.getId() == groupForm.getCategoryId()){
                selectedTag = current.getName();
            }
        }
        mCategorysView.setTagList(categoryTags);
        mCategorysView.setOnSelectedListener(new TagSelectedView.OnSelectedListener() {
            @Override
            public void onSelected(int index, String tag) {
            }
        });
        mCategorysView.setSelectedTag(selectedTag);
    }

    @OnClick(R.id.title_btn_back)
    void onClickBack() {
        finish();
    }

    @OnClick(R.id.title_btn_next)
    void onClickSave() {
    }

    private String mAvatarPath;

    /**
     * 点击头像, 添加群头像
     */
    @OnClick(R.id.avatar)
    void onClickAvatar() {
        final PickerOption.CropParams params = new PickerOption.CropParams();
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
                mAvatarPath = imageItem.path;
                //String uri = Scheme.wrapUri("file", imageItem.path);
                //ImageManager.displayImage(uri, mAvatarView, AvatarHelper.getGroupOptions());
                Bitmap bitmap = BitmapFactory.decodeFile(imageItem.path);
                mAvatarView.setImageDrawable(new CircleDrawable(bitmap));
                //ImageUtil.loadCircleImage(getBaseContext(), mAvatarView, "",  );
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
