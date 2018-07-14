package com.agmbat.menu;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.agmbat.android.SysResources;
import com.agmbat.android.utils.DeviceUtils;
import com.agmbat.isdialog.ISDialogController;
import com.agmbat.isdialog.R;

import java.util.List;

/**
 * GridMenu dialog
 */
public class GridMenu extends Dialog implements AdapterView.OnItemClickListener {

    /**
     * 存储参数
     */
    private ISDialogController mController;

    public GridMenu(Context context) {
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
        GridView grid = createShareGridView(getContext());
        setContentView(grid);

        grid.setAdapter(new GridAdapter(getContext(), mController.getSheetItems()));
        int screenWidth = DeviceUtils.getScreenSize().x;
        // 设置Dialog最小宽度为屏幕宽度
        grid.setMinimumWidth(screenWidth);
        mController.installContent();
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

    public void addItem(MenuInfo menuInfo) {
        mController.addItem(menuInfo);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MenuInfo menuInfo = (MenuInfo) parent.getItemAtPosition(position);
        if (menuInfo != null) {
            menuInfo.performClick(position);
        }
        dismiss();
    }

    private GridView createShareGridView(final Context context) {
        GridView grid = new GridView(context);
        grid.setNumColumns(5);
        grid.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        grid.setColumnWidth((int) SysResources.dipToPixel(80));
        grid.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        grid.setSelector(R.drawable.grid_menu_background);
        grid.setOnItemClickListener(this);
        grid.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        return grid;
    }

    private static class GridAdapter extends ArrayAdapter<MenuInfo> {

        public GridAdapter(@NonNull Context context, List<MenuInfo> list) {
            super(context, 0, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.grid_menu_item, parent, false);
            ImageView image = (ImageView) view.findViewById(android.R.id.icon);
            TextView platform = (TextView) view.findViewById(android.R.id.title);
            MenuInfo menuInfo = getItem(position);
            image.setImageDrawable(menuInfo.getIcon());
            platform.setText(menuInfo.getTitle());
            return view;
        }
    }

}
