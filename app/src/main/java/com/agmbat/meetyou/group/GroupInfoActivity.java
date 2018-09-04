package com.agmbat.meetyou.group;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.agmbat.android.SysResources;
import com.agmbat.android.image.ImageManager;
import com.agmbat.android.utils.ToastUtil;
import com.agmbat.android.utils.WindowUtils;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.group.CircleInfo;
import com.agmbat.imsdk.group.DismissGroupReply;
import com.agmbat.imsdk.group.JoinGroupIQ;
import com.agmbat.imsdk.group.JoinGroupReply;
import com.agmbat.imsdk.group.QueryGroupInfoIQ;
import com.agmbat.imsdk.group.QueryGroupInfoResultIQ;
import com.agmbat.imsdk.group.QuitGroupReplay;
import com.agmbat.imsdk.search.group.GroupInfo;
import com.agmbat.log.Log;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.chat.ChatActivity;
import com.agmbat.meetyou.helper.AvatarHelper;
import com.agmbat.meetyou.search.ViewUserHelper;
import com.agmbat.menu.MenuInfo;
import com.agmbat.menu.OnClickMenuListener;
import com.agmbat.menu.PopupMenu;
import com.google.zxing.client.android.encode.QRCodeEncoder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.XmppStringUtils;
import org.jivesoftware.smackx.circle.DismissCirclePacket;
import org.jivesoftware.smackx.circle.ExitCirclePacket;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 群信息显示界面
 */
public class GroupInfoActivity extends Activity {

    public static final String KEY_GROUP = "group";
    public static final String KEY_GROUP_JID = "group_jid";

    @BindView(R.id.avatar)
    ImageView mAvatarView;
    @BindView(R.id.nickname)
    TextView mNickNameTv;
    @BindView(R.id.category_name)
    TextView mCategoryTv;
    @BindView(R.id.owner_name)
    TextView mOwnerNameTv;
    @BindView(R.id.description)
    TextView mDescriptionTv;
    @BindView(R.id.im_uid)
    TextView mGroupIdTv;
    @BindView(R.id.member_num)
    TextView mMemberNumTv;

    @BindView(R.id.qr_code)
    ImageView mQrCodeImageView;
    @BindView(R.id.btn_quit_group)
    TextView mBtnQuitGroup;
    @BindView(R.id.btn_edit_group)
    TextView mBtnEditGroup;
    @BindView(R.id.btn_join_group)
    TextView mBtnJoinGroup;

    private String mGroupJid;
    private GroupInfo mGroupInfo;

    public static void launch(Context context, GroupInfo groupInfo) {
        Intent intent = new Intent(context, GroupInfoActivity.class);
        intent.putExtra(KEY_GROUP, groupInfo);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void launch(Context context, String groupJid) {
        Intent intent = new Intent(context, GroupInfoActivity.class);
        intent.putExtra(KEY_GROUP_JID, groupJid);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, getResources().getColor(R.color.bg_status_bar));
        setContentView(R.layout.activity_group_info);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        XMPPManager.getInstance().getXmppConnection().addPacketListener(mJoinGroupListener, new PacketTypeFilter(JoinGroupReply.class));
        XMPPManager.getInstance().getXmppConnection().addPacketListener(mQuitGroupListener, new PacketTypeFilter(QuitGroupReplay.class));
        XMPPManager.getInstance().getXmppConnection().addPacketListener(mDismissGroupListener, new PacketTypeFilter(DismissGroupReply.class));
        XMPPManager.getInstance().getXmppConnection().addPacketListener(mQueryGroupInfoListener, new PacketTypeFilter(QueryGroupInfoResultIQ.class));

        Intent intent = getIntent();
        if (intent.hasExtra(KEY_GROUP)) {
            mGroupInfo = (GroupInfo) intent.getSerializableExtra(KEY_GROUP);
            setupViews(mGroupInfo);
            mGroupJid = mGroupInfo.jid;
        } else if (intent.hasExtra(KEY_GROUP_JID)) {
            mGroupJid = intent.getStringExtra(KEY_GROUP_JID);
        }
        fillGroupQrCodeImage(mGroupJid);
        loadGroupInfo();
    }

    private void loadGroupInfo() {
        QueryGroupInfoIQ queryGroupInfoIQ = new QueryGroupInfoIQ(mGroupJid);
        XMPPManager.getInstance().getXmppConnection().sendPacket(queryGroupInfoIQ);
    }

    private PacketListener mJoinGroupListener = new PacketListener() {
        @Override
        public void processPacket(Packet packet) {
            if (packet instanceof JoinGroupReply) {
                JoinGroupReply joinGroupReply = (JoinGroupReply) packet;
                EventBus.getDefault().post(joinGroupReply);
            }
        }
    };

    private PacketListener mQuitGroupListener = new PacketListener() {
        @Override
        public void processPacket(Packet packet) {
            if (packet instanceof QuitGroupReplay) {
                QuitGroupReplay quitGroupReplay = (QuitGroupReplay) packet;
                if (quitGroupReplay.isSuccess()) {
                    ToastUtil.showToast("退群成功");
                    EventBus.getDefault().post(new RemoveGroupEvent(mGroupJid));
                } else {
                    ToastUtil.showToast("退群失败，请重试");
                }
            }
        }
    };

    private PacketListener mDismissGroupListener = new PacketListener() {
        @Override
        public void processPacket(Packet packet) {
            if (packet instanceof DismissGroupReply) {
                DismissGroupReply dismissGroupReply = (DismissGroupReply) packet;
                if (dismissGroupReply.isSuccess()) {
                    ToastUtil.showToast("解散群成功");
                    EventBus.getDefault().post(new RemoveGroupEvent(mGroupJid));
                } else {
                    ToastUtil.showToast("解散群失败，请重试");
                }
            }
        }
    };


    private PacketListener mQueryGroupInfoListener = new PacketListener() {
        @Override
        public void processPacket(Packet packet) {
            if (packet instanceof QueryGroupInfoResultIQ) {
                QueryGroupInfoResultIQ result = (QueryGroupInfoResultIQ) packet;
                GroupInfo groupInfo = new GroupInfo();
                groupInfo.categoryName = result.getCategory();
                groupInfo.cover = result.getAvatar();
                groupInfo.description = result.getDescription();
                groupInfo.jid = result.getGroupJid();
                groupInfo.ownerJid = result.getOwner();
                groupInfo.ownerName = result.getOwnerNickName();
                groupInfo.name = result.getName();
                groupInfo.memberNum = result.getMembers();
                groupInfo.isGroupMember = result.isGroupMember();
                EventBus.getDefault().post(groupInfo);
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GroupInfo result) {
        Log.d("Rcv group info: " + result.toString());
        mGroupInfo = result;
        setupViews(mGroupInfo);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(JoinGroupReply joinGroupReply) {
        if (joinGroupReply.isSuccess()) {
            ToastUtil.showToast("你已成功入群");
            mBtnJoinGroup.setVisibility(View.VISIBLE);
            mBtnJoinGroup.setText("进入群聊");
            mBtnQuitGroup.setVisibility(View.GONE);
            mBtnEditGroup.setVisibility(View.GONE);
            mGroupInfo.memberNum++;
            mGroupInfo.isGroupMember = true;
            mMemberNumTv.setText(String.valueOf(mGroupInfo.memberNum));
        } else if (joinGroupReply.isWaitForAgree()) {
            ToastUtil.showToast("已申请成功，等待群主审批");
        } else {
            ToastUtil.showToast("该群聊不存在");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RemoveGroupEvent removeGroupEvent) {
        if (removeGroupEvent.getGroupJid().equals(mGroupJid)) {
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EditGroupEvent editGroupEvent) {
        //收到群修改成功通知后，刷新群信息
        if (editGroupEvent.getGroupJid().equals(mGroupJid)) {
            loadGroupInfo();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        XMPPManager.getInstance().getXmppConnection().removePacketListener(mJoinGroupListener);
        XMPPManager.getInstance().getXmppConnection().removePacketListener(mQueryGroupInfoListener);
        XMPPManager.getInstance().getXmppConnection().removePacketListener(mQuitGroupListener);
        XMPPManager.getInstance().getXmppConnection().removePacketListener(mDismissGroupListener);
    }

    private void fillGroupQrCodeImage(String groupJid) {
        int size = (int) SysResources.dipToPixel(300);
        Bitmap bitmap = QRCodeEncoder.encode(groupJid, size);
        mQrCodeImageView.setImageBitmap(bitmap);
    }

    /**
     * 点击返回键
     */
    @OnClick(R.id.title_btn_back)
    void onClickBack() {
        finish();
    }

    private void setupViews(GroupInfo groupInfo) {
        ImageManager.displayImage(groupInfo.cover, mAvatarView, AvatarHelper.getGroupOptions());
        mNickNameTv.setText(groupInfo.name);
        mCategoryTv.setText(groupInfo.categoryName);
        mOwnerNameTv.setText(groupInfo.ownerName);
        mDescriptionTv.setText(groupInfo.description);
        mGroupIdTv.setText(XmppStringUtils.parseName(groupInfo.jid));
        mMemberNumTv.setText(String.valueOf(groupInfo.memberNum));
        if (!groupInfo.isGroupMember) {//当前登录用户还不是群成员，只显示加群按钮
            mBtnJoinGroup.setVisibility(View.VISIBLE);
            mBtnQuitGroup.setVisibility(View.GONE);
            mBtnEditGroup.setVisibility(View.GONE);
        } else {
            mBtnJoinGroup.setVisibility(View.GONE);
            mBtnQuitGroup.setVisibility(View.VISIBLE);
            String loginUser = XMPPManager.getInstance().getXmppConnection().getBareJid();
            String ownerJid = XmppStringUtils.parseBareAddress(mGroupInfo.ownerJid);
            if (loginUser.equals(ownerJid)) { //如果是群主，则可以执行解散群的操作
                mBtnQuitGroup.setText(R.string.label_btn_dismiss_group);
                mBtnEditGroup.setVisibility(View.VISIBLE);
            } else {
                mBtnQuitGroup.setText(R.string.label_btn_quit_group);//一般用户只能执行退群操作
                mBtnEditGroup.setVisibility(View.GONE);
            }
            mBtnQuitGroup.setVisibility(View.VISIBLE);
        }
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
        intent.putExtra(ViewUserHelper.KEY_USER_INFO, mGroupJid);
        startActivity(intent);
    }

    @OnClick(R.id.item_member_num)
    public void onClickMemberNum() {
        if (null == mGroupInfo) {
            return;
        }
        GroupMembersActivity.launch(getBaseContext(), mGroupJid, mGroupInfo.ownerJid);
    }


    @OnClick(R.id.btn_quit_group)
    public void onClickQuitBtn() {
        String loginUser = XMPPManager.getInstance().getXmppConnection().getBareJid();
        String ownerJid = XmppStringUtils.parseBareAddress(mGroupInfo.ownerJid);
        if (ownerJid.equals(loginUser)) { //如果是群主则执行解散群操作
            XMPPManager.getInstance().getXmppConnection().sendPacket(new DismissCirclePacket(mGroupJid));
        } else {
            XMPPManager.getInstance().getXmppConnection().sendPacket(new ExitCirclePacket(mGroupJid));
        }
    }

    @OnClick(R.id.btn_edit_group)
    public void onClickEditGroupBtn() {
        EditGroupActivity.launch(this, mGroupJid);
    }

    @OnClick(R.id.btn_join_group)
    public void onClickJoinGroupBtn() {
        if (mGroupInfo.isGroupMember) {
            CircleInfo circleInfo = new CircleInfo(mGroupInfo.jid, mGroupInfo.name);
            circleInfo.setAvatar(mGroupInfo.cover);
            circleInfo.setMembers(mGroupInfo.memberNum);
            circleInfo.setOwnerJid(mGroupInfo.ownerJid);
            ChatActivity.openGroupChat(this, circleInfo);
        } else {
            XMPPConnection xmppConnection = XMPPManager.getInstance().getXmppConnection();
            String senderName = XmppStringUtils.parseName(xmppConnection.getUser());
            JoinGroupIQ joinGroupIQ = new JoinGroupIQ(senderName);
            joinGroupIQ.setType(IQ.Type.SET);
            joinGroupIQ.setTo(mGroupJid);
            xmppConnection.sendPacket(joinGroupIQ);
        }

    }
}
