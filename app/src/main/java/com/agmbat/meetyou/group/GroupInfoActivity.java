package com.agmbat.meetyou.group;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.agmbat.android.SysResources;
import com.agmbat.android.image.ImageManager;
import com.agmbat.android.utils.ToastUtil;
import com.agmbat.android.utils.WindowUtils;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.group.DismissGroupReply;
import com.agmbat.imsdk.group.JoinGroupIQ;
import com.agmbat.imsdk.group.JoinGroupReply;
import com.agmbat.imsdk.group.QueryGroupInfoIQ;
import com.agmbat.imsdk.group.QueryGroupInfoResultIQ;
import com.agmbat.imsdk.group.QuitGroupReplay;
import com.agmbat.imsdk.search.group.GroupInfo;
import com.agmbat.meetyou.R;
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

    @BindView(R.id.qr_code)
    ImageView mQrCodeImageView;
    @BindView(R.id.btn_quit_group)
    TextView mBtnQuitGroup;

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
        } else if (intent.hasExtra(KEY_GROUP_JID)) {
            mGroupJid = intent.getStringExtra(KEY_GROUP_JID);
        }

        if (null != mGroupInfo) {
            setupViews(mGroupInfo);
            fillGroupQrCodeImage(mGroupInfo.jid);
        } else if (!TextUtils.isEmpty(mGroupJid)) {
            QueryGroupInfoIQ queryGroupInfoIQ = new QueryGroupInfoIQ(mGroupJid);
            XMPPManager.getInstance().getXmppConnection().sendPacket(queryGroupInfoIQ);
            fillGroupQrCodeImage(mGroupJid);
        }
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
                    EventBus.getDefault().post(new QuitGroupEvent());
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
                    EventBus.getDefault().post(new QuitGroupEvent());
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
                EventBus.getDefault().post(result);
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(QueryGroupInfoResultIQ result) {
        mGroupInfo = new GroupInfo();
        mGroupInfo.categoryName = result.getCategory();
        mGroupInfo.cover = result.getAvatar();
        mGroupInfo.description = result.getDescription();
        mGroupInfo.jid = result.getGroupJid();
        mGroupInfo.ownerJid = result.getOwner();
        mGroupInfo.ownerName = XmppStringUtils.parseName(result.getOwner());
        mGroupInfo.name = result.getName();
        mGroupInfo.memberNum = result.getMembers();
        setupViews(mGroupInfo);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(JoinGroupReply joinGroupReply) {
        if (joinGroupReply.isSuccess()) {
            ToastUtil.showToast("你已成功入群");
        } else if (joinGroupReply.isWaitForAgree()) {
            ToastUtil.showToast("已申请成功，等待群主审批");
        } else {
            ToastUtil.showToast("该群聊不存在");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(QuitGroupEvent joinGroupReply) {
        finish();
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

        TextView memberNumView = (TextView) findViewById(R.id.member_num);
        memberNumView.setText(String.valueOf(groupInfo.memberNum));

        String loginUser = XMPPManager.getInstance().getXmppConnection().getBareJid();
        String ownerJid = XmppStringUtils.parseBareAddress(mGroupInfo.ownerJid);
        if(loginUser.equals(ownerJid)){
            mBtnQuitGroup.setText(R.string.label_btn_dismiss_group);
        }else{
            mBtnQuitGroup.setText(R.string.label_btn_quit_group);
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

        MenuInfo joinGroup = new MenuInfo();
        joinGroup.setTitle(getString(R.string.title_join_group));
        joinGroup.setOnClickMenuListener(new OnClickMenuListener() {
            @Override
            public void onClick(MenuInfo menu, int index) {
                XMPPConnection xmppConnection = XMPPManager.getInstance().getXmppConnection();
                String senderName = XmppStringUtils.parseName(xmppConnection.getUser());
                JoinGroupIQ joinGroupIQ = new JoinGroupIQ(senderName);
                joinGroupIQ.setType(IQ.Type.SET);
                joinGroupIQ.setTo(mGroupJid);
                xmppConnection.sendPacket(joinGroupIQ);
            }
        });

        popupMenu.addItem(reportUser);
        popupMenu.addItem(joinGroup);

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
        GroupMembersActivity.launch(getBaseContext(), mGroupJid);
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

}
