package com.agmbat.menu;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.agmbat.android.utils.DeviceUtils;
import com.agmbat.isdialog.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 标题栏下的pop menu
 */
public class PopupMenu extends PopupWindow implements OnItemClickListener {

    private static final String TAG = PopupMenu.class.getSimpleName();

    /**
     * 列表弹窗的间隔
     */
    private static final int LIST_PADDING = 10;

    private final Context mContext;

    private final PopupListAdapter mAdapter;

    public PopupMenu(Context context) {
        super(context);
        mContext = context;
        // 设置可以获得焦点
        setFocusable(true);
        // 设置弹窗内可点击
        setTouchable(true);
        // 设置弹窗外可点击
        setOutsideTouchable(true);

        setWidth(LayoutParams.WRAP_CONTENT);
        setHeight(LayoutParams.WRAP_CONTENT);

        setBackgroundDrawable(new BitmapDrawable());

        setContentView(View.inflate(mContext, R.layout.popup_menu, null));
        setAnimationStyle(R.style.PopupLeftBelowAnimation);

        mAdapter = new PopupListAdapter(context, new ArrayList<MenuInfo>());
        ListView listView = (ListView) getContentView().findViewById(android.R.id.list);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);
    }

    /**
     * 显示弹窗列表界面
     */
    public void show(View view) {
        // 获得点击屏幕的位置坐标
        final int[] location = new int[2];
        view.getLocationOnScreen(location);
        // 设置矩形的大小
        Rect rect = new Rect();
        rect.set(location[0], location[1], location[0] + view.getWidth(), location[1] + view.getHeight());
        // 显示弹窗的位置
        int screenWidth = DeviceUtils.getScreenSize().x;
        // 显示的位置在右边
        showAtLocation(view, Gravity.NO_GRAVITY, screenWidth - LIST_PADDING - (getWidth() / 2), rect.bottom);
    }

    public void addItem(int itemRes, OnClickMenuListener l) {
        addItem(mContext.getString(itemRes), l);
    }

    public void addItem(CharSequence item, OnClickMenuListener l) {
        addItem(new MenuInfo(item, l));
    }

    public void addItem(MenuInfo menuInfo) {
        mAdapter.add(menuInfo);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MenuInfo menuInfo = mAdapter.getItem(position);
        if (menuInfo != null) {
            menuInfo.performClick(position);
        }
        dismiss();
    }

    public static class PopupListAdapter extends ArrayAdapter<MenuInfo> {

        public PopupListAdapter(Context context, List<MenuInfo> items) {
            super(context, 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.popup_menu_item, null);
            }
            MenuInfo menuInfo = getItem(position);
            TextView itemView = (TextView) convertView.findViewById(android.R.id.text1);
            itemView.setText(menuInfo.getTitle());
            if (menuInfo.getIcon() != null) {
                // 设置在文字的左边放一个图标
                itemView.setCompoundDrawablesWithIntrinsicBounds(menuInfo.getIcon(), null, null, null);
            }
            return convertView;
        }

    }
}
