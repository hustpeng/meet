package com.agmbat.meetyou.blocklist;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.agmbat.android.utils.ToastUtil;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.isdialog.ISAlertDialog;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.account.RegisterSuccessEvent;
import com.agmbat.meetyou.widget.OnRecyclerViewItemClickListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smackx.block.BlockListener;
import org.jivesoftware.smackx.block.BlockManager;
import org.jivesoftware.smackx.block.BlockObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BlockListActivity extends Activity {

    @BindView(R.id.black_list_view)
    RecyclerView mBlackListView;

    private BlockListAdapter mBlockListAdapter;
    private BlockManager mBlockManager;

    public static void launch(Context context) {
        Intent intent = new Intent(context, BlockListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_list);
        ButterKnife.bind(this);
        initContentView();
        loadBlockList();
    }

    private void initContentView() {
        mBlockListAdapter = new BlockListAdapter(getApplicationContext());
        mBlockListAdapter.setOnItemClickListener(mOnItemClickListener);
        mBlackListView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        mBlackListView.setAdapter(mBlockListAdapter);
    }

    private void loadBlockList() {
        mBlockManager = XMPPManager.getInstance().getBlockManager();
        mBlockManager.addListener(mBlockListener);
        mBlockManager.fetchBlockList();
        List<BlockObject> blockObjects = mBlockManager.getAllBlockObjects();
        mBlockListAdapter.setAll(blockObjects);
    }


    private BlockListener mBlockListener = new BlockListener() {
        @Override
        public void notifyFetchBlockListNameResult(boolean success) {


        }

        @Override
        public void notifyFetchBlockResult(final boolean success) {
            if (success) {
                List<BlockObject> blockObjects = mBlockManager.getAllBlockObjects();
                mBlockListAdapter.setAll(blockObjects);
            }
        }

        @Override
        public void notifyAddBlockResult(String jid, boolean success) {

        }

        @Override
        public void notifyRemoveBlockResult(String jid, boolean success) {
            if(jid.equals(mRemoveJid)){
                if(success){
                    ToastUtil.showToast("已移出黑名单");
                    mBlockManager.setActiveName(mBlockManager.getListName());
                    mBlockListAdapter.setAll(mBlockManager.getAllBlockObjects());
                }else{
                    ToastUtil.showToast("移出黑名单失败");
                }
            }
        }

        @Override
        public void notifyBlockListChange(String jid, boolean isBlock) {

        }
    };

    private String mRemoveJid = "";

    private OnRecyclerViewItemClickListener mOnItemClickListener = new OnRecyclerViewItemClickListener() {
        @Override
        public void onItemClick(View view, int position, RecyclerView.ViewHolder viewHolder) {

        }

        @Override
        public void onLongClick(View view, int position, RecyclerView.ViewHolder viewHolder) {
            final BlockObject blockObject = mBlockListAdapter.getItem(position);
            if(null == blockObject){
                ToastUtil.showToast("找不到需要移出的人");
                return;
            }
            mRemoveJid = blockObject.getJid();

            final AlertDialog.Builder builder = new AlertDialog.Builder(BlockListActivity.this);
            CharSequence[] items = new CharSequence[]{"移出黑名单"};
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(which == 0){
                        mBlockManager.removeBlock(mRemoveJid);
                    }
                }
            });
            builder.create().show();
        }
    };

    @OnClick(R.id.title_btn_back)
    public void onClickBack() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBlockManager.removeListener(mBlockListener);
    }
}
