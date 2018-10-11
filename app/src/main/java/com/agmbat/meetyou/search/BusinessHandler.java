package com.agmbat.meetyou.search;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.agmbat.android.utils.ToastUtil;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.asmack.roster.ContactInfo;
import com.agmbat.isdialog.ISAlertDialog;
import com.agmbat.isdialog.ISLoadingDialog;
import com.agmbat.meetyou.R;
import com.agmbat.menu.MenuInfo;
import com.agmbat.menu.OnClickMenuListener;
import com.agmbat.menu.PopupMenu;

import org.jivesoftware.smackx.block.BlockListener;
import org.jivesoftware.smackx.block.BlockManager;

/**
 * 查看用户信息界面 , 业务处理
 */
public abstract class BusinessHandler {

    protected ContactInfo mContactInfo;

    public BusinessHandler(ContactInfo contactInfo) {
        mContactInfo = contactInfo;
    }

    public ContactInfo getContactInfo() {
        return mContactInfo;
    }

    /**
     * 配置view显示
     *
     * @param view
     */
    public void setupViews(View view){
        boolean isFriend = XMPPManager.getInstance().getRosterManager().isFriend(mContactInfo.getBareJid());
        if(isFriend){
            view.findViewById(R.id.setup_alias).setVisibility(View.VISIBLE);
            view.findViewById(R.id.btn_add_to_contact).setVisibility(View.GONE);
            view.findViewById(R.id.btn_chat).setVisibility(View.VISIBLE);
        }else{
            view.findViewById(R.id.setup_alias).setVisibility(View.GONE);
            view.findViewById(R.id.btn_add_to_contact).setVisibility(View.VISIBLE);
            view.findViewById(R.id.btn_chat).setVisibility(View.GONE);
        }
    }

    /**
     * 准备menu显示
     *
     * @param popupMenu
     */
    public void onPrepareMoreMenu(final Context context, PopupMenu popupMenu, final ContactInfo contactInfo) {
        boolean isFriend = XMPPManager.getInstance().getRosterManager().isFriend(mContactInfo.getBareJid());
        if (isFriend) {
            MenuInfo reportUser = new MenuInfo();
            reportUser.setTitle("删除好友");
            reportUser.setOnClickMenuListener(new OnClickMenuListener() {
                @Override
                public void onClick(MenuInfo menu, int index) {
                    showRemoveUserDialog(context, contactInfo);
                }
            });
            popupMenu.addItem(reportUser);

            final BlockManager blockManager = XMPPManager.getInstance().getBlockManager();
            blockManager.addListener(mBlockListener);
            final boolean isUserBlock = blockManager.isBlock(contactInfo.getBareJid());
            MenuInfo blockUser = new MenuInfo();
            blockUser.setTitle(isUserBlock ? "移出黑名单" : "加入黑名单");
            blockUser.setOnClickMenuListener(new OnClickMenuListener() {
                @Override
                public void onClick(MenuInfo menu, int index) {
                    if (isUserBlock) {
                        blockManager.removeBlock(contactInfo.getBareJid());
                    } else {
                        blockManager.addBlock(contactInfo.getBareJid());
                    }
                }
            });
            popupMenu.addItem(blockUser);
        }
    }

    private BlockListener mBlockListener = new BlockListener() {
        @Override
        public void notifyFetchBlockListNameResult(boolean success) {

        }

        @Override
        public void notifyFetchBlockResult(boolean success) {

        }

        @Override
        public void notifyAddBlockResult(String jid, boolean success) {
            BlockManager blockManager = XMPPManager.getInstance().getBlockManager();
            if (success) {
                ToastUtil.showToast("已加入黑名单");
                blockManager.setActiveName(blockManager.getListName());
            } else {
                ToastUtil.showToast("加入黑名单失败");
            }
            blockManager.removeListener(this);
        }

        @Override
        public void notifyRemoveBlockResult(String jid, boolean success) {
            BlockManager blockManager = XMPPManager.getInstance().getBlockManager();
            if (success) {
                ToastUtil.showToast("已移出黑名单");
            } else {
                ToastUtil.showToast("移出黑名单失败");
            }
            blockManager.removeListener(this);
        }

        @Override
        public void notifyBlockListChange(String jid, boolean isBlock) {

        }
    };

    private void showRemoveUserDialog(final Context context, final ContactInfo contactInfo) {
        ISAlertDialog dialog = new ISAlertDialog(context);
        dialog.setTitle("删除好友");
        dialog.setMessage("将好友删除，同时删除与该好友的聊天记录");
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeUser(context, contactInfo);
            }
        });
        dialog.setNegativeButton("取消", null);
        dialog.show();
    }

    /**
     * 删除好友
     *
     * @param context
     * @param contactInfo
     */
    private void removeUser(Context context, ContactInfo contactInfo) {
        ISLoadingDialog dialog = new ISLoadingDialog(context);
        dialog.setMessage("正在删除好友");
        boolean success = XMPPManager.getInstance().getRosterManager().deleteContact(contactInfo);
        dialog.dismiss();
        if (success) {
            ToastUtil.showToast("删除好友成功");
            if(context instanceof Activity){
                Activity activity = (Activity) context;
                activity.finish();
            }
        } else {
            ToastUtil.showToast("删除好友失败");
        }
    }

}
