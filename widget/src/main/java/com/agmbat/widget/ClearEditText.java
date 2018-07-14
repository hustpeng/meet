package com.agmbat.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.agmbat.android.SysResources;

/**
 * 带清空功能的EditText
 */
public class ClearEditText extends EditText {

    private Drawable mDrawable;

    public ClearEditText(Context context) {
        super(context);
        init();
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        Resources res = getResources();
        mDrawable = res.getDrawable(R.drawable.widget_edit_clear);
        mDrawable.setBounds(0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());
        tryShowClearDrawable();
        setMinHeight((mDrawable.getIntrinsicHeight() + (int) SysResources.dipToPixel(5)));
        setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (getCompoundDrawables()[2] == null) {
                    return false;
                }
                if ((event.getAction() != MotionEvent.ACTION_UP)
                        || (event.getX() <= getWidth() - getPaddingRight()
                        - mDrawable.getIntrinsicWidth())) {
                    return false;
                }
                setText("");
                hideClearDrawable();
                return false;
            }
        });
        addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tryShowClearDrawable();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

        setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                tryShowClearDrawable();
            }

        });
    }

    private void tryShowClearDrawable() {
        if ((getText().toString().equals("")) || (!isFocused())) {
            hideClearDrawable();
            return;
        }
        final Drawable[] drawables = getCompoundDrawables();
        setCompoundDrawables(drawables[0], drawables[1], mDrawable, drawables[3]);
    }

    private void hideClearDrawable() {
        final Drawable[] drawables = getCompoundDrawables();
        setCompoundDrawables(drawables[0], drawables[1], null, drawables[3]);
    }

}
