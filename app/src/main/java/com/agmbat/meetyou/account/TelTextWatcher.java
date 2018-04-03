package com.agmbat.meetyou.account;

import android.text.Editable;
import android.text.TextWatcher;

import com.agmbat.text.PhoneNumberUtil;

/**
 * 手机号 EditText监听器
 */
public class TelTextWatcher implements TextWatcher {

    public interface OnInputTelephoneListener {

        /**
         * 检测输出电话号码
         *
         * @param complete    是否输入了11位电话
         * @param isTelephone 是否为电话号码
         */
        public void onInputTelephone(boolean complete, boolean isTelephone);
    }

    private OnInputTelephoneListener mListener;

    public TelTextWatcher(OnInputTelephoneListener l) {
        mListener = l;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        String phone = s.toString();
        if (phone.length() == 11) {
            if (PhoneNumberUtil.isValidPhoneNumber(phone)) {
                if (mListener != null) {
                    mListener.onInputTelephone(true, true);
                }
            } else {
                if (mListener != null) {
                    mListener.onInputTelephone(true, false);
                }
            }
        } else {
            if (mListener != null) {
                mListener.onInputTelephone(false, false);
            }
        }
    }
}
