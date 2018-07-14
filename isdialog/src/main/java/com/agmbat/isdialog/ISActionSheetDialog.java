package com.agmbat.isdialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.agmbat.android.SysResources;
import com.agmbat.android.utils.DeviceUtils;
import com.agmbat.menu.MenuInfo;
import com.agmbat.menu.OnClickMenuListener;

import java.util.List;

/**
 * iOS style dialog
 */
public class ISActionSheetDialog extends Dialog {


    private LinearLayout mContentView;

    private ScrollView mScrollView;

    /**
     * 存储参数
     */
    private ISDialogController mController;

    public ISActionSheetDialog(Context context) {
        super(context, R.style.ISDialogActionSheet);
        mController = new ISDialogController(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.x = 0;
        lp.y = 0;
        window.setAttributes(lp);

        // 获取Dialog布局
        View view = LayoutInflater.from(getContext()).inflate(R.layout.isdialog_actionsheet, null);
        setContentView(view);

        int screenWidth = DeviceUtils.getScreenSize().x;
        // 设置Dialog最小宽度为屏幕宽度
        view.setMinimumWidth(screenWidth);

        // 获取自定义Dialog布局中的控件
        mScrollView = (ScrollView) view.findViewById(R.id.scroll_view);
        mContentView = (LinearLayout) view.findViewById(R.id.layout_content);
        // 定义Dialog布局和参数
        mController.installContent();
        applyItems();
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

    @Override
    public void setCancelable(boolean flag) {
        super.setCancelable(flag);
    }

    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(cancel);
    }

    /**
     * 使用取消Button
     */
    public void useNegativeButton() {
        CharSequence text = getContext().getText(R.string.isdlg_cancel);
        setNegativeButton(text, null);
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

    /**
     * 添加一条选项
     *
     * @param text     条目名称
     * @param listener
     * @return
     */
    public void addItem(String text, OnClickMenuListener listener) {
        mController.addItem(text, listener);
    }

    /**
     * 添加一条选项
     *
     * @param text     条目名称
     * @param color    条目字体颜色，设置null则默认蓝色
     * @param listener
     * @return
     */
    public void addItem(String text, int color, OnClickMenuListener listener) {
        mController.addItem(text, color, listener);
    }

    @Override
    public void show() {
        super.show();
    }

    /**
     * 设置条目布局
     */
    private void applyItems() {
        List<MenuInfo> list = mController.getSheetItems();
        int size = list.size();
        if (size <= 0) {
            return;
        }

        // TODO 高度控制，非最佳解决办法
        // 添加条目过多的时候控制高度
        if (size >= 7) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mScrollView.getLayoutParams();
            int screenHeight = DeviceUtils.getScreenSize().y;
            params.height = screenHeight / 2;
            mScrollView.setLayoutParams(params);
        }

        boolean showTitle = mController.isShowTitle();
        // 循环添加条目
        for (int i = 0; i < size; i++) {
            MenuInfo sheetItem = list.get(i);
            TextView textView = createItemView(sheetItem, size, showTitle, i);
            mContentView.addView(textView);
        }
    }

    /**
     * 创建item
     *
     * @param item
     * @param size
     * @param showTitle
     * @param index
     * @return
     */
    private TextView createItemView(final MenuInfo item, int size, boolean showTitle, final int index) {
        TextView tv = new TextView(getContext());
        tv.setText(item.getTitle());
        tv.setTextSize(18);
        tv.setGravity(Gravity.CENTER);
        // 字体颜色
        tv.setTextColor(item.getTitleColor());
        int txtBgRes = getItemBackgroundResource(size, index, showTitle);
        tv.setBackgroundResource(txtBgRes);

        // 高度
        int height = (int) SysResources.dipToPixel(45);
        tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));
        // 点击事件
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.getOnClickMenuListener() != null) {
                    item.getOnClickMenuListener().onClick(item, index);
                }
                dismiss();
            }
        });
        return tv;
    }

    /**
     * 获取背景资源
     *
     * @param size
     * @param index
     * @param showTitle
     * @return
     */
    private int getItemBackgroundResource(int size, int index, boolean showTitle) {
        // 背景图片
        if (size == 1) {
            // 如果只有一个元素
            if (showTitle) {
                return R.drawable.actionsheet_bottom_selector;
            } else {
                return R.drawable.actionsheet_single_selector;
            }
        }
        // 如果有多个元素
        int resId;
        if (showTitle) {
            if (index >= 0 && index < size - 1) {
                resId = R.drawable.actionsheet_middle_selector;
            } else {
                resId = R.drawable.actionsheet_bottom_selector;
            }
        } else {
            if (index == 0) {
                resId = R.drawable.actionsheet_top_selector;
            } else if (index < size - 1) {
                resId = R.drawable.actionsheet_middle_selector;
            } else {
                // 最后一个
                resId = R.drawable.actionsheet_bottom_selector;
            }
        }
        return resId;
    }

}
