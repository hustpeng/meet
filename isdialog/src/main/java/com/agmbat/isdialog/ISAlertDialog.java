package com.agmbat.isdialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.agmbat.android.utils.DeviceUtils;

/**
 * iOS style alert dialog
 */
public class ISAlertDialog extends Dialog {

    private ImageView mButtonDivider;

    /**
     * 存储参数
     */
    private ISDialogController mController;

    public ISAlertDialog(Context context) {
        super(context, R.style.AlertDialog);
        mController = new ISDialogController(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);

        // 获取Dialog布局
        View view = LayoutInflater.from(getContext()).inflate(R.layout.isdialog_alert, null);
        setContentView(view);

        // 获取自定义Dialog布局中的控件
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.lLayout_bg);
        // 调整dialog背景大小
        int screenWidth = DeviceUtils.getScreenSize().x;
        int width = (int) (screenWidth * 0.85);
        layout.setLayoutParams(new FrameLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT));

        mButtonDivider = (ImageView) view.findViewById(R.id.img_line);
        mController.installContent();
        updateButtonPosition();
    }

    /**
     * 设置标题
     *
     * @param title
     * @return
     */
    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        mController.setTitle(title);
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

    @Override
    public void setCancelable(boolean flag) {
        super.setCancelable(flag);
    }

    public void setPositiveButton(CharSequence text, final OnClickListener listener) {
        mController.setPositiveButton(text, listener);
    }

    /**
     * Set a listener to be invoked when the negative button of the dialog is pressed.
     *
     * @param text     The text to display in the negative button
     * @param listener The {@link OnClickListener} to use.
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public void setNegativeButton(CharSequence text, final OnClickListener listener) {
        mController.setNegativeButton(text, listener);
    }

    private void updateButtonPosition() {
        // 如果需要显示两个Button
        if (mController.isShowPositiveButton() && mController.isShowNegativeButton()) {
            mController.getPositiveButton().setBackgroundResource(R.drawable.alertdialog_right_selector);
            mController.getNegativeButton().setBackgroundResource(R.drawable.alertdialog_left_selector);
            mButtonDivider.setVisibility(View.VISIBLE);
            return;
        }
        mButtonDivider.setVisibility(View.GONE);
        if (mController.isShowPositiveButton()) {
            mController.getPositiveButton().setBackgroundResource(R.drawable.alertdialog_single_selector);
            return;
        } else if (mController.isShowNegativeButton()) {
            mController.getNegativeButton().setBackgroundResource(R.drawable.alertdialog_single_selector);
        }

    }
}
