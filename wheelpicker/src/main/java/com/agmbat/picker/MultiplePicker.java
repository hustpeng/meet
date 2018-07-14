package com.agmbat.picker;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.agmbat.picker.popup.ConfirmPopup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 多项选择器
 */
public class MultiplePicker extends ConfirmPopup<ScrollView> {

    private List<String> items = new ArrayList<>();
    private LinearLayout layout;
    private OnItemPickListener onItemPickListener;

    public MultiplePicker(Context context, String[] items) {
        this(context, Arrays.asList(items));
    }

    public MultiplePicker(Context context, List<String> items) {
        super(context);
        this.items = items;
    }

    public void setOnItemPickListener(OnItemPickListener onItemPickListener) {
        this.onItemPickListener = onItemPickListener;
    }

    @NonNull
    @Override
    protected ScrollView makeCenterView() {
        ScrollView scrollView = new ScrollView(mContext);
        layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);
        for (String item : items) {
            LinearLayout line = new LinearLayout(mContext);
            line.setOrientation(LinearLayout.HORIZONTAL);
            line.setGravity(Gravity.CENTER);
            TextView textView = new TextView(mContext);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1.0f);
            lp.gravity = Gravity.CENTER;
            textView.setLayoutParams(lp);
            textView.setText(item);
            textView.setGravity(Gravity.CENTER);
            line.addView(textView);
            CheckBox checkBox = new CheckBox(mContext);
            checkBox.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, 0.4f));
            line.addView(checkBox);
            layout.addView(line);
        }
        scrollView.addView(layout);
        return scrollView;
    }

    @Override
    protected void onSubmit() {
        if (onItemPickListener == null) {
            return;
        }
        List<String> checked = new ArrayList<>();
        for (int i = 0, count = layout.getChildCount(); i < count; i++) {
            LinearLayout line = (LinearLayout) layout.getChildAt(i);
            CheckBox checkBox = (CheckBox) line.getChildAt(1);
            if (checkBox.isChecked()) {
                TextView textView = (TextView) line.getChildAt(0);
                checked.add(textView.getText().toString());
            }
        }
        onItemPickListener.onItemPicked(checked.size(), checked);
    }

    public interface OnItemPickListener {

        void onItemPicked(int count, List<String> items);

    }

}
