package com.agmbat.meetyou.group;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.agmbat.android.SysResources;
import com.agmbat.android.utils.ToastUtil;
import com.agmbat.android.utils.WindowUtils;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.group.GroupMember;
import com.agmbat.imsdk.group.KickMemberReply;
import com.agmbat.imsdk.group.QueryGroupMembersIQ;
import com.agmbat.imsdk.group.QueryGroupMembersReply;
import com.agmbat.imsdk.group.TransOwnerIQ;
import com.agmbat.imsdk.group.TransOwnerReply;
import com.agmbat.log.Log;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.widget.OnRecyclerViewItemClickListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.circle.KickMemberPacket;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GroupMembersActivity extends Activity {


    private static final String EXTRA_GROUP_JID = "group_jid";
    private static final String EXTRA_OWNER_JID = "owner_jid";

    @BindView(R.id.member_list)
    RecyclerView mMemberListView;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    private GroupMembersAdapter mGroupMemberAdapter;
    private String mGroupJid;
    private static final int SPAN_COUNT = 5;
    private String mOwnerJid;

    public static void launch(Context context, String jid, String ownerJid) {
        Intent intent = new Intent(context, GroupMembersActivity.class);
        intent.putExtra(EXTRA_GROUP_JID, jid);
        intent.putExtra(EXTRA_OWNER_JID, ownerJid);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, getResources().getColor(R.color.bg_status_bar));
        mGroupJid = getIntent().getStringExtra(EXTRA_GROUP_JID);
        mOwnerJid = getIntent().getStringExtra(EXTRA_OWNER_JID);
        setContentView(R.layout.activity_group_members);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        XMPPManager.getInstance().getXmppConnection().addPacketListener(mGroupMembersListener, new PacketTypeFilter(QueryGroupMembersReply.class));
        XMPPManager.getInstance().getXmppConnection().addPacketListener(mKickMembersListener, new PacketTypeFilter(KickMemberReply.class));
        XMPPManager.getInstance().getXmppConnection().addPacketListener(mTransOwnerListener, new PacketTypeFilter(TransOwnerReply.class));
        initContentView();
        loadMembers();
    }


    private void initContentView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getBaseContext(), SPAN_COUNT);
        mMemberListView.setLayoutManager(gridLayoutManager);
        mGroupMemberAdapter = new GroupMembersAdapter(getBaseContext());

        final String loginUser = XMPPManager.getInstance().getXmppConnection().getBareJid();
        if (loginUser.equals(mOwnerJid)) { //是群主允许执行操作
            mGroupMemberAdapter.setOnItemClickListener(mOnGroupMemberListener);
        }
        mMemberListView.setAdapter(mGroupMemberAdapter);
        mMemberListView.addItemDecoration(new MembersDivider());
    }


    private OnRecyclerViewItemClickListener mOnGroupMemberListener = new OnRecyclerViewItemClickListener<GroupMembersAdapter.GroupMemberHolder>() {

        @Override
        public void onItemClick(View view, int position, GroupMembersAdapter.GroupMemberHolder viewHolder) {

        }

        @Override
        public void onLongClick(View view, int position, GroupMembersAdapter.GroupMemberHolder viewHolder) {
            final GroupMember groupMember = mGroupMemberAdapter.getItem(position);
            //不对群主执行操作
            if (GroupMember.ROLE_OWNER.equals(groupMember.getRole())) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(GroupMembersActivity.this);
            String[] operations = new String[]{"踢出群", "提升为群主"};
            builder.setItems(operations, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    XMPPConnection xmppConnection = XMPPManager.getInstance().getXmppConnection();
                    if (!xmppConnection.isConnected()) {
                        return;
                    }
                    if (which == 0) {
                        KickMemberPacket kickMemberPacket = new KickMemberPacket(mGroupJid);
                        kickMemberPacket.setMember(groupMember.getJid());
                        kickMemberPacket.setReason("Kick Out");
                        kickMemberPacket.setFrom(xmppConnection.getBareJid());
                        xmppConnection.sendPacket(kickMemberPacket);
                    } else if (which == 1) {
                        TransOwnerIQ transOwnerIQ = new TransOwnerIQ(mGroupJid, groupMember.getJid());
                        xmppConnection.sendPacket(transOwnerIQ);

                    }
                }
            });
            builder.create().show();
        }
    };

    private class MembersDivider extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            outRect.top = (int) SysResources.dipToPixel(15);
        }
    }

    private void loadMembers() {
        XMPPConnection xmppConnection = XMPPManager.getInstance().getXmppConnection();
        if (xmppConnection.isConnected()) {
            xmppConnection.sendPacket(new QueryGroupMembersIQ(mGroupJid));
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private PacketListener mGroupMembersListener = new PacketListener() {
        @Override
        public void processPacket(Packet packet) {
            if (packet instanceof QueryGroupMembersReply) {
                QueryGroupMembersReply resultIQ = (QueryGroupMembersReply) packet;
                EventBus.getDefault().post(resultIQ);
            }
        }
    };



    private PacketListener mKickMembersListener = new PacketListener() {
        @Override
        public void processPacket(Packet packet) {
            if (packet instanceof KickMemberReply) {
                KickMemberReply resultIQ = (KickMemberReply) packet;
                mGroupMemberAdapter.removeMember(resultIQ.getMember());
                ToastUtil.showToast(String.format("%s已被踢出", resultIQ.getMember()));
            }
        }
    };

    private PacketListener mTransOwnerListener = new PacketListener() {
        @Override
        public void processPacket(Packet packet) {
            if (packet instanceof TransOwnerReply) {
                TransOwnerReply resultIQ = (TransOwnerReply) packet;
                ToastUtil.showToast(String.format("%s已被提为群主", resultIQ.getNewOwner()));
            }
        }
    };


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(QueryGroupMembersReply groupMembersReply) {
        Log.d("Rcv group members: " + groupMembersReply.getGroupMembers().size());
        mGroupMemberAdapter.setAll(groupMembersReply.getGroupMembers());
        mProgressBar.setVisibility(View.GONE);
    }

    @OnClick(R.id.title_btn_back)
    public void onClickBack() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        XMPPManager.getInstance().getXmppConnection().removePacketListener(mKickMembersListener);
        XMPPManager.getInstance().getXmppConnection().removePacketListener(mTransOwnerListener);
        XMPPManager.getInstance().getXmppConnection().removePacketListener(mGroupMembersListener);
    }
}
