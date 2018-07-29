package com.agmbat.meetyou.group;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.agmbat.android.SysResources;
import com.agmbat.android.utils.AppUtils;
import com.agmbat.android.utils.ToastUtil;
import com.agmbat.android.utils.WindowUtils;
import com.agmbat.imagepicker.ImagePickerHelper;
import com.agmbat.imagepicker.OnPickImageListener;
import com.agmbat.imagepicker.PickerOption;
import com.agmbat.imagepicker.bean.ImageItem;
import com.agmbat.imagepicker.view.CropImageView;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.group.CreateGroupIQ;
import com.agmbat.imsdk.group.CreateGroupResultIQ;
import com.agmbat.imsdk.remotefile.FileApiResult;
import com.agmbat.imsdk.remotefile.OnFileUploadListener;
import com.agmbat.imsdk.remotefile.RemoteFileManager;
import com.agmbat.imsdk.search.SearchManager;
import com.agmbat.imsdk.search.group.GroupCategory;
import com.agmbat.imsdk.search.group.GroupCategoryResult;
import com.agmbat.imsdk.search.group.OnGetGroupCategoryListener;
import com.agmbat.isdialog.ISAlertDialog;
import com.agmbat.isdialog.ISLoadingDialog;
import com.agmbat.log.Log;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.discovery.search.TagSelectedView;
import com.agmbat.meetyou.util.CircleDrawable;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 创建群界面
 */
public class CreateGroupActivity extends Activity {

    /**
     * 群分类控件
     */
    @BindView(R.id.tag_selected_view)
    TagSelectedView mTagSelectedView;

    /**
     * 群头像
     */
    @BindView(R.id.avatar)
    ImageView mAvatarView;

    @BindView(R.id.checkbox)
    CheckBox mCheckBox;

    @BindView(R.id.page1)
    View mPage1;

    @BindView(R.id.page2)
    View mPage2;

    @BindView(R.id.title_btn_next)
    TextView mNextButton;


    /**
     * 输出群名称
     */
    @BindView(R.id.input_nickname)
    EditText mInputNicknameView;

    @BindView(R.id.group_category_name)
    TextView mGroupCategoryNameView;

    @BindView(R.id.verify_checkbox)
    CheckBox mVerifyCheckbox;

    /**
     * 群描述输入框
     */
    @BindView(R.id.input_description)
    EditText mInputDescriptionView;


    /**
     * 立即创建Button
     */
    @BindView(R.id.btn_create_group)
    Button mCreateGroupButton;
    /**
     * 群头像地址
     */
    private String mAvatarPath;

    /**
     * 索引
     */
    private int mSelectedGroupIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, getResources().getColor(R.color.bg_status_bar));
        setContentView(R.layout.activity_create_group);
        ButterKnife.bind(this);
        XMPPManager.getInstance().getXmppConnection().addPacketListener(mGroupCreateListener, new PacketTypeFilter(CreateGroupResultIQ.class));
        mCheckBox.setChecked(false);
        mVerifyCheckbox.setChecked(false);

        mTagSelectedView.setOnSelectedListener(new TagSelectedView.OnSelectedListener() {
            @Override
            public void onSelected(int index, String tag) {
                mSelectedGroupIndex = index;
            }
        });
        loadGroupCategories();
        showPage1();
    }


    private List<GroupCategory> mGroupCategories;

    private void loadGroupCategories() {
        List<GroupCategory> cachedGroupCategories = GroupDBCache.getGroupCategories();
        if ((null == cachedGroupCategories || cachedGroupCategories.size() == 0)) {
            SearchManager.getGroupCategory(new OnGetGroupCategoryListener() {
                @Override
                public void onGetGroupCategory(GroupCategoryResult result) {
                    if (result.mResult && null != result.mData) {
                        GroupDBCache.saveGroupCategories(result.mData);
                        fillGroupTags(result.mData);
                    }
                }

            });
        } else {
            fillGroupTags(cachedGroupCategories);
        }
    }


    private void fillGroupTags(List<GroupCategory> groupTags) {
        List<String> tagsText = new ArrayList<>();
        for (int i = 0; i < groupTags.size(); i++) {
            tagsText.add(groupTags.get(i).getName());
        }
        mTagSelectedView.setTagList(tagsText);
        if (groupTags.size() > 0) {
            mTagSelectedView.setSelectedTag(groupTags.size() - 1);
        }
        mGroupCategories = groupTags;
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
        if (mPage2.isShown()) {
            showPage1();
            return;
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        onClickBack();
    }

    /**
     * 点击下一步
     */
    @OnClick(R.id.title_btn_next)
    void onClickNext() {
        String groupName = mInputNicknameView.getText().toString().trim();
        if (TextUtils.isEmpty(groupName)) {
            ToastUtil.showToast("请输入群名称");
            return;
        }
        if (mCheckBox.isChecked()) {
            showPage2();
        } else {
            ISAlertDialog dialog = new ISAlertDialog(this);
            dialog.setMessage("请阅读并同意服务声明");
            dialog.setPositiveButton("确定", null);
            dialog.show();
        }
    }

    /**
     * 点击声明
     */
    @OnClick(R.id.statement)
    void onClickStatement() {
        AppUtils.openBrowser(this, "http://www.baidu.com");
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


    @OnClick(R.id.btn_create_group)
    void onClickCreateGroup() {
        if (mSelectedGroupIndex < 0 || mSelectedGroupIndex >= mGroupCategories.size()) {
            ToastUtil.showToast("请选择一个群分类");
            return;
        }
        showCreateProgressDialog();
        GroupCategory groupCategory = mGroupCategories.get(mSelectedGroupIndex);
        CreateGroupIQ createGroupIQ = new CreateGroupIQ(mInputNicknameView.getText().toString(), mVerifyCheckbox.isChecked(), groupCategory.getId());
        createGroupIQ.setDescription(mInputDescriptionView.getText().toString());
        createGroupIQ.setTo(XMPPManager.GROUP_CHAT_SERVER);
        createGroupIQ.setType(IQ.Type.SET);
        XMPPConnection xmppConnection = XMPPManager.getInstance().getXmppConnection();
        if (xmppConnection.isConnected()) {
            xmppConnection.sendPacket(createGroupIQ);
        }
    }

    private ISLoadingDialog mCreateProgressDialog;

    private void showCreateProgressDialog() {
        if (null == mCreateProgressDialog || !mCreateProgressDialog.isShowing()) {
            // 添加loading框
            mCreateProgressDialog = new ISLoadingDialog(this);
            mCreateProgressDialog.setMessage("正在创建群聊...");
            mCreateProgressDialog.setCancelable(false);
            mCreateProgressDialog.show();
        }
    }

    private void dismissCreateProgressDialog() {
        if (null != mCreateProgressDialog && mCreateProgressDialog.isShowing()) {
            mCreateProgressDialog.dismiss();
            mCreateProgressDialog = null;
        }
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
                dismissCreateProgressDialog();
                finish();
                ToastUtil.showToast("成功创建群聊");
                Log.d("Upload group avatar result: " + apiResult.mResult, ", errorMsg: " + apiResult.mErrorMsg);
            }
        });
    }


    private PacketListener mGroupCreateListener = new PacketListener() {

        @Override
        public void processPacket(Packet packet) {
            if (packet instanceof CreateGroupResultIQ) {
                CreateGroupResultIQ createGroupIQ = (CreateGroupResultIQ) packet;
                Log.d("Create group success, groupJid: " + createGroupIQ.getGroupJid());
                if (TextUtils.isEmpty(createGroupIQ.getGroupJid())) {
                    ToastUtil.showToast("群聊创建失败");
                    return;
                }

                if (!TextUtils.isEmpty(mAvatarPath)) {
                    uploadGroupAvatar(mAvatarPath, createGroupIQ.getGroupJid());
                } else {
                    ToastUtil.showToast("成功创建群聊");
                    dismissCreateProgressDialog();
                    finish();
                }
            }
        }
    };


    private void showPage1() {
        mNextButton.setVisibility(View.VISIBLE);
        mPage1.setVisibility(View.VISIBLE);
        mPage2.setVisibility(View.INVISIBLE);
    }

    private void showPage2() {
        mGroupCategoryNameView.setText(mTagSelectedView.getSelectedTag());

        mNextButton.setVisibility(View.INVISIBLE);
        mPage1.setVisibility(View.INVISIBLE);
        mPage2.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        XMPPManager.getInstance().getXmppConnection().removePacketListener(mGroupCreateListener);
    }
}
