/*
 * Copyright (C) 2015 mayimchen <mayimchen@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.agmbat.android.utils;

import android.app.Activity;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.agmbat.log.Log;

/**
 * 控件操作工具类
 */
public class ViewUtils {

    private static final String TAG = ViewUtils.class.getSimpleName();

    public static int getListViewHeaderCount(final ListAdapter adapter) {
        int headerCount = 0;
        if (adapter instanceof HeaderViewListAdapter) {
            final HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) adapter;
            headerCount = headerViewListAdapter.getHeadersCount();
        }
        return headerCount;
    }

    /**
     * Get the actual adapter of adapter view
     *
     * @param adapterView
     * @return the actual adapter
     */
    public static ListAdapter getActualAdapter(AdapterView<?> adapterView) {
        ListAdapter adapter = (ListAdapter) adapterView.getAdapter();
        if (adapter instanceof HeaderViewListAdapter) {
            adapter = ((HeaderViewListAdapter) adapter).getWrappedAdapter();
        }
        return adapter;
    }

    /**
     * Get child of the parent view, which is same type with given ViewClass.
     */
    public static View getChildByType(ViewGroup parent, Class<? extends View> viewClass) {
        if (parent != null) {
            for (int i = 0; i < parent.getChildCount(); i++) {
                View child = parent.getChildAt(i);
                if (child.getClass() == viewClass) {
                    return child;
                }
            }
        }
        return null;
    }

    public static void transformSelfLocationToParent(View view, View parent, int[] location) {
        int offsetX = view.getLeft();
        int offsetY = view.getTop();
        // child's left and top already have scroll information of parent, don't need to re-add.
        location[0] += offsetX;
        location[1] += offsetY;
    }

    public static void transformParentLocationToSelf(View view, View parent, int[] location) {
        int offsetX = -view.getLeft();
        int offsetY = -view.getTop();
        // parent location is not aware of child's scroll, so add them
        location[0] += (offsetX + view.getScrollX());
        location[1] += (offsetY + view.getScrollY());
    }

    /**
     * calculates the approximate width of a text, depending on a demo text avoid repeated calls (e.g. inside drawing
     * methods)
     *
     * @param paint
     * @param demoText
     * @return
     */
    public static int calcTextWidth(Paint paint, String demoText) {
        return (int) paint.measureText(demoText);
    }

    /**
     * calculates the approximate height of a text, depending on a demo text avoid repeated calls (e.g. inside drawing
     * methods)
     *
     * @param paint
     * @param demoText
     * @return
     */
    public static int calcTextHeight(Paint paint, String demoText) {
        Rect r = new Rect();
        paint.getTextBounds(demoText, 0, demoText.length(), r);
        return r.height();
    }

    /**
     * Gets the scrollable parent.
     *
     * @return the scrollable parent
     */
    public static ViewGroup getScrollableParent(View target) {
        while (true) {
            ViewParent vp = target.getParent();
            if (vp == null) {
                return null;
            }
            // ViewRootImpl cannot be cast to android.view.View
            if (vp instanceof View) {
                View parent = (View) vp;
                if (parent instanceof ListView || parent instanceof ScrollView) {
                    return (ViewGroup) parent;
                }
                target = parent;
            }
            return null;
        }
    }

    /**
     * Returns the position within the adapter's dataset for the view, where view is an adapter item or a descendant of
     * an adapter item. Unlike {@link AdapterView#getPositionForView(android.view.View)}, returned position will reflect
     * the position of the item given view is representing, by subtracting the header views count.
     *
     * @param absListView the ListView containing the view.
     * @param view        an adapter item or a descendant of an adapter item. This must be visible in given
     *                    AdapterView at the
     *                    time of the call.
     * @return the position of the item in the AdapterView represented by given view, or
     * {@link AdapterView#INVALID_POSITION} if the view does not correspond to a list item (or it is not
     * visible).
     */
    public static int getPositionForView(final AbsListView absListView, final View view) {
        int position = absListView.getPositionForView(view);
        if (absListView instanceof ListView) {
            position -= ((ListView) absListView).getHeaderViewsCount();
        }
        return position;
    }

    /**
     * Returns the {@link View} that represents the item for given position.
     *
     * @param absListView the ListView that should be examined
     * @param position    the position for which the {@code View} should be returned.
     * @return the {@code View}, or {@code null} if the position is not currently visible.
     */
    public static View getViewForPosition(final AbsListView absListView, final int position) {
        int childCount = absListView.getChildCount();
        View downView = null;
        for (int i = 0; i < childCount && downView == null; i++) {
            View child = absListView.getChildAt(i);
            if (child != null && getPositionForView(absListView, child) == position) {
                downView = child;
            }
        }
        return downView;
    }


    public static void addView(View view, ViewGroup.LayoutParams params, WindowManager manager) {
        try {
            manager.addView(view, params);
        } catch (Exception e) {
            Log.e(TAG, e);
        }
    }

    public static void removeView(View view, WindowManager manager) {
        try {
            manager.removeView(view);
        } catch (Exception e) {
            Log.e(TAG, e);
        }
    }

    public static void updatePaddingByBackground(View view) {
        if (view == null) {
            return;
        }
        Drawable background = view.getBackground();
        if (background == null) {
            return;
        }
        Rect rect = new Rect();
        background.getPadding(rect);
        view.setPadding(rect.left, rect.top, rect.right, rect.bottom);
    }


    public static int getFontHeight(TextView textView) {
        Paint paint = new Paint();
        paint.setTextSize(textView.getTextSize());
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (int) Math.ceil(fm.bottom - fm.top);
    }

    public static View getRootView(Activity context) {
        return ((ViewGroup) context.findViewById(android.R.id.content)).getChildAt(0);
    }

//    /**
//     * 使用反射设置tabIndicator的宽度
//     *
//     * @param tabs
//     * @param leftDip
//     * @param rightDip
//     */
//    public static void setIndicator(TabLayout tabs, int leftDip, int rightDip) {
//        Class<?> tabLayout = tabs.getClass();
//        Field tabStrip = null;
//        try {
//            tabStrip = tabLayout.getDeclaredField("mTabStrip");
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        }
//
//        tabStrip.setAccessible(true);
//        LinearLayout llTab = null;
//        try {
//            llTab = (LinearLayout) tabStrip.get(tabs);
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//
//        int left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftDip, Resources.getSystem().getDisplayMetrics());
//        int right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightDip, Resources.getSystem().getDisplayMetrics());
//
//        for (int i = 0; i < llTab.getChildCount(); i++) {
//            View child = llTab.getChildAt(i);
//            child.setPadding(0, 0, 0, 0);
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
//            params.leftMargin = left;
//            params.rightMargin = right;
//            child.setLayoutParams(params);
//            child.invalidate();
//        }
//    }
}
