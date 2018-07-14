package com.agmbat.emoji.display;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public abstract class EmoticonFilter implements TextWatcher {

    private EditText mEditText;

    public EmoticonFilter(EditText text) {
        mEditText = text;
    }

    public abstract void filter(EditText editText, CharSequence text, int start, int lengthBefore, int lengthAfter);

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }


    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        filter(mEditText, s, start, before, count);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
