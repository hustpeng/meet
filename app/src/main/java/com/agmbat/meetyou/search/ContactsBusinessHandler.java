package com.agmbat.meetyou.search;

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

/**
 * 查看联系人信息界面处理
 */
public class ContactsBusinessHandler extends BusinessHandler {

    public ContactsBusinessHandler(ContactInfo contactInfo) {
        super(contactInfo);
    }

    @Override
    public void setupViews(View view) {
        view.findViewById(R.id.setup_alias).setVisibility(View.VISIBLE);
        view.findViewById(R.id.btn_add_to_contact).setVisibility(View.GONE);
        view.findViewById(R.id.btn_pass_validation).setVisibility(View.GONE);
        view.findViewById(R.id.btn_chat).setVisibility(View.VISIBLE);
    }

    @Override
    public void onPrepareMoreMenu(final Context context, PopupMenu popupMenu, final ContactInfo contactInfo) {
        super.onPrepareMoreMenu(context, popupMenu, contactInfo);
        MenuInfo reportUser = new MenuInfo();
        reportUser.setTitle("删除好友");
        reportUser.setOnClickMenuListener(new OnClickMenuListener() {
            @Override
            public void onClick(MenuInfo menu, int index) {
                showRemoveUserDialog(context, contactInfo);
            }
        });
        popupMenu.addItem(reportUser);
    }

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

        } else {
            ToastUtil.showToast("删除好友失败");
        }
    }
}
