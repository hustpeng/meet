package com.agmbat.meetyou.group;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.agmbat.android.image.ImageManager;
import com.agmbat.android.utils.AppUtils;
import com.agmbat.android.utils.ToastUtil;
import com.agmbat.android.utils.WindowUtils;
import com.agmbat.imagepicker.ImagePickerHelper;
import com.agmbat.imagepicker.OnPickImageListener;
import com.agmbat.imagepicker.PickerOption;
import com.agmbat.imagepicker.bean.ImageItem;
import com.agmbat.imagepicker.view.CropImageView;
import com.agmbat.isdialog.ISAlertDialog;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.discovery.search.TagSelectedView;
import com.agmbat.meetyou.helper.AvatarHelper;
import com.nostra13.universalimageloader.core.download.Scheme;

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
     * 群类名称
     */
    private String mGroupCategory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, getResources().getColor(R.color.bg_status_bar));
        setContentView(R.layout.activity_create_group);
        ButterKnife.bind(this);
        mCheckBox.setChecked(false);
        mVerifyCheckbox.setChecked(true);

        String uri = Scheme.wrapUri("drawable", String.valueOf(R.drawable.selector_image_add));
        ImageManager.displayImage(uri, mAvatarView, AvatarHelper.getGroupOptions());

        List<String> tagList = new ArrayList<>();
        tagList.add("单身");
        tagList.add("已婚");
        tagList.add("兴趣");
        tagList.add("娱乐");
        tagList.add("职场");
        tagList.add("商务");
        tagList.add("学习");
        tagList.add("生活");
        tagList.add("同城");
        tagList.add("同乡");
        tagList.add("同学");
        tagList.add("战友");
        tagList.add("其它");
        mTagSelectedView.setTagList(tagList);
        mTagSelectedView.setSelectedTag("其它");
        mTagSelectedView.setOnSelectedListener(new TagSelectedView.OnSelectedListener() {
            @Override
            public void onSelected(String tag) {
                mGroupCategory = tag;
            }
        });


        showPage1();
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
        params.setOutPutX(200);//保存文件的宽度。单位像素
        params.setOutPutY(200);//保存文件的高度。单位像素 }

        PickerOption option = new PickerOption();
        option.setShowCamera(false);
        option.setCropParams(params);

        ImagePickerHelper.pickImage(this, option, new OnPickImageListener() {
            @Override
            public void onPickImage(ImageItem imageItem) {
                mAvatarPath = imageItem.path;
                String uri = Scheme.wrapUri("file", imageItem.path);
                ImageManager.displayImage(uri, mAvatarView, AvatarHelper.getGroupOptions());
            }
        });
    }


    @OnClick(R.id.btn_create_group)
    void onClickCreateGroup() {

    }

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
}
