package com.agmbat.isdialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

/**
 * iOS Style loading
 */
public class ISLoadingDialog extends Dialog {

    private ISDialogController mController;

    public ISLoadingDialog(Context context) {
        super(context, R.style.ISDialogLoading);
        mController = new ISDialogController(this);
        // 默认情况loading框点击到外部不消失
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.isdialog_loading);
        mController.installContent();
    }

    /**
     * 设置提示信息
     *
     * @param message
     * @return
     */
    public void setMessage(CharSequence message) {
        mController.setMessage(message);
    }

    /**
     * 设置是否可以按返回键取消
     *
     * @param flag
     * @return
     */
    @Override
    public void setCancelable(boolean flag) {
        super.setCancelable(flag);
    }

    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(cancel);
    }

}
