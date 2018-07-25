package com.agmbat.meetyou.group;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.agmbat.android.SysResources;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.group.QueryGroupMembersIQ;
import com.agmbat.imsdk.group.QueryGroupMembersResultIQ;
import com.agmbat.meetyou.R;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupMembersActivity extends Activity {


    private static final String EXTRA_GROUP_JID = "group_jid";

    @BindView(R.id.member_list)
    RecyclerView mMemberListView;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    private GroupMembersAdapter mGroupMemberAdapter;
    private String mGroupJid;
    private static final int SPAN_COUNT = 5;

    public static void launch(Context context, String jid) {
        Intent intent = new Intent(context, GroupMembersActivity.class);
        intent.putExtra(EXTRA_GROUP_JID, jid);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGroupJid = getIntent().getStringExtra(EXTRA_GROUP_JID);
        setContentView(R.layout.activity_group_members);
        ButterKnife.bind(this);
        XMPPManager.getInstance().getXmppConnection().addPacketListener(mGroupMembersListener, new PacketTypeFilter(QueryGroupMembersResultIQ.class));
        initContentView();
        loadMembers();
    }


    private void initContentView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getBaseContext(), SPAN_COUNT);
        mMemberListView.setLayoutManager(gridLayoutManager);
        mGroupMemberAdapter = new GroupMembersAdapter(getBaseContext());
        mMemberListView.setAdapter(mGroupMemberAdapter);
        mMemberListView.addItemDecoration(new MembersDivider());
    }

    private class MembersDivider extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            outRect.top = (int) SysResources.dipToPixel(15);
        }
    }

    private void loadMembers() {
        XMPPManager.getInstance().getXmppConnection().sendPacket(new QueryGroupMembersIQ(mGroupJid));
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private PacketListener mGroupMembersListener = new PacketListener() {
        @Override
        public void processPacket(Packet packet) {
            if (packet instanceof QueryGroupMembersResultIQ) {
                QueryGroupMembersResultIQ resultIQ = (QueryGroupMembersResultIQ) packet;
                mGroupMemberAdapter.setAll(resultIQ.getGroupMembers());
                mProgressBar.setVisibility(View.GONE);
            }
        }
    };
}
