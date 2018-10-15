package com.agmbat.isdialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.agmbat.menu.MenuInfo;
import com.agmbat.menu.OnClickMenuListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 对话框Controller
 */
public class ISDialogController {

    /**
     * 对话框
     */
    private final Dialog mDialog;
    /**
     * 标题栏
     */
    private TextView mTitleView;
    /**
     * 消息
     */
    private TextView mMessageView;
    /**
     * 确认 Button
     */
    private Button mPositiveButton;
    /**
     * 取消
     */
    private TextView mNegativeButton;
    /**
     * 标题
     */
    private CharSequence mTitle;

    /**
     * 消息内容
     */
    private CharSequence mMessage;

    private CharSequence mNegativeButtonText;
    private DialogInterface.OnClickListener mNegativeButtonListener;

    private CharSequence mPositiveButtonText;
    private DialogInterface.OnClickListener mPositiveButtonListener;

    private List<MenuInfo> mItemList = new ArrayList<MenuInfo>();

    /**
     * 取消Button的点击事件
     */
    private View.OnClickListener mNegativeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mDialog.dismiss();
            clickNegative(mDialog);
        }
    };

    /**
     * 取消Button的点击事件
     */
    private View.OnClickListener mPositiveListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mDialog.dismiss();
            clickPositive(mDialog);
        }
    };

    public ISDialogController(Dialog dialog) {
        mDialog = dialog;
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public void setTitle(CharSequence title) {
        mTitle = title;
        applyTitle();
    }

    /**
     * 设置消息
     *
     * @param message
     */
    public void setMessage(CharSequence message) {
        mMessage = message;
        applyMessage();
    }

    public void setPositiveButton(CharSequence text, DialogInterface.OnClickListener listener) {
        mPositiveButtonText = text;
        mPositiveButtonListener = listener;
        applyPositiveButton();
    }

    public void setNegativeButton(CharSequence text, DialogInterface.OnClickListener listener) {
        mNegativeButtonText = text;
        mNegativeButtonListener = listener;
        applyNegativeButton();
    }

    public void addItem(String text, OnClickMenuListener listener) {
        mItemList.add(new MenuInfo(text, listener));
    }

    public void addItem(String text, int color, OnClickMenuListener listener) {
        mItemList.add(new MenuInfo(text, color, listener));
    }

    /**
     * 添加menu
     *
     * @param menuInfo
     */
    public void addItem(MenuInfo menuInfo) {
        mItemList.add(menuInfo);
    }

    /**
     * 安装内容
     */
    public void installContent() {
        mTitleView = (TextView) mDialog.findViewById(R.id.title);
        mMessageView = (TextView) mDialog.findViewById(R.id.message);
        mPositiveButton = (Button) mDialog.findViewById(R.id.positive);
        mNegativeButton = (TextView) mDialog.findViewById(R.id.negative);

        applyTitle();
        applyMessage();
        applyPositiveButton();
        applyNegativeButton();
    }

    /**
     * 设置标题
     */
    private void applyTitle() {
        if (mTitleView == null) {
            return;
        }
        mTitleView.setText(mTitle);
        if (TextUtils.isEmpty(mTitle)) {
            mTitleView.setVisibility(View.GONE);
        } else {
            mTitleView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置消息提示
     */
    private void applyMessage() {
        if (mMessageView == null) {
            return;
        }
        mMessageView.setText(mMessage);
        if (TextUtils.isEmpty(mMessage)) {
            mMessageView.setVisibility(View.GONE);
        } else {
            mMessageView.setVisibility(View.VISIBLE);
        }
    }

    private void applyPositiveButton() {
        if (mPositiveButton == null) {
            return;
        }
        mPositiveButton.setText(mPositiveButtonText);
        if (TextUtils.isEmpty(mPositiveButtonText)) {
            mPositiveButton.setVisibility(View.GONE);
            mPositiveButton.setOnClickListener(null);
        } else {
            mPositiveButton.setVisibility(View.VISIBLE);
            mPositiveButton.setOnClickListener(mPositiveListener);
        }
    }

    /**
     * 配置取消Button
     */
    private void applyNegativeButton() {
        if (mNegativeButton == null) {
            return;
        }
        mNegativeButton.setText(mNegativeButtonText);
        if (TextUtils.isEmpty(mNegativeButtonText)) {
            mNegativeButton.setVisibility(View.GONE);
            mNegativeButton.setOnClickListener(null);
        } else {
            mNegativeButton.setVisibility(View.VISIBLE);
            mNegativeButton.setOnClickListener(mNegativeListener);
        }
    }

    /**
     * 点击Negative Button
     *
     * @param dialog
     */
    private void clickNegative(DialogInterface dialog) {
        if (mNegativeButtonListener != null) {
            mNegativeButtonListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
        }
    }

    /**
     * 点击Positive Button
     *
     * @param dialog
     */
    private void clickPositive(DialogInterface dialog) {
        if (mPositiveButtonListener != null) {
            mPositiveButtonListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
        }
    }

    public TextView getPositiveButton() {
        return mPositiveButton;
    }

    public TextView getNegativeButton() {
        return mNegativeButton;
    }

    public List<MenuInfo> getSheetItems() {
        return mItemList;
    }

    /**
     * 是否显示title
     *
     * @return
     */
    public boolean isShowTitle() {
        return !TextUtils.isEmpty(mTitle);
    }

    public boolean isShowPositiveButton() {
        return !TextUtils.isEmpty(mPositiveButtonText);
    }

    public boolean isShowNegativeButton() {
        return !TextUtils.isEmpty(mNegativeButtonText);
    }


}
