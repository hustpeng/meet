package com.agmbat.meetyou.group;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.agmbat.android.image.ImageManager;
import com.agmbat.android.utils.WindowUtils;
import com.agmbat.imsdk.search.group.GroupInfo;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.helper.AvatarHelper;
import com.agmbat.meetyou.search.ReportUserActivity;
import com.agmbat.meetyou.search.ViewUserHelper;
import com.agmbat.menu.MenuInfo;
import com.agmbat.menu.OnClickMenuListener;
import com.agmbat.menu.PopupMenu;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 群信息显示界面
 */
public class GroupInfoActivity extends Activity {

    private GroupInfo mGroupInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, getResources().getColor(R.color.bg_status_bar));
        setContentView(R.layout.activity_group_info);
        ButterKnife.bind(this);
        mGroupInfo = GroupInfoHelper.getGroupInfo(getIntent());
        setupViews(mGroupInfo);
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

    private void setupViews(GroupInfo groupInfo) {
        ImageView avatarView = (ImageView) findViewById(R.id.avatar);
        ImageManager.displayImage(groupInfo.cover, avatarView, AvatarHelper.getGroupOptions());

        TextView nickNameView = (TextView) findViewById(R.id.nickname);
        nickNameView.setText(groupInfo.name);

        TextView categoryNameView = (TextView) findViewById(R.id.category_name);
        categoryNameView.setText(groupInfo.categoryName);

        TextView ownerNameView = (TextView) findViewById(R.id.owner_name);
        ownerNameView.setText(groupInfo.ownerName);

        TextView descriptionView = (TextView) findViewById(R.id.description);
        descriptionView.setText(groupInfo.description);

        TextView imUidView = (TextView) findViewById(R.id.im_uid);
        imUidView.setText(String.valueOf(groupInfo.imUid));

        TextView isHotView = (TextView) findViewById(R.id.is_hot);
        isHotView.setText(groupInfo.isHot == 1 ? "是" : "否");

        TextView memberNumView = (TextView) findViewById(R.id.member_num);
        memberNumView.setText(String.valueOf(groupInfo.memberNum));
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
                reportGroup();
            }
        });
        popupMenu.addItem(reportUser);

        View v = (View) view.getParent();
        popupMenu.show(v);
    }

    /**
     * 举报群
     */
    private void reportGroup() {
        Intent intent = new Intent(this, ReportGroupActivity.class);
        intent.putExtra(ViewUserHelper.KEY_USER_INFO, mGroupInfo.jid);
        startActivity(intent);
    }

}
