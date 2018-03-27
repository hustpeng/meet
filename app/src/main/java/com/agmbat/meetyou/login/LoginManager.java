package com.agmbat.meetyou.login;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.agmbat.android.task.AsyncTask;
import com.agmbat.android.task.AsyncTaskUtils;
import com.agmbat.android.utils.ToastUtil;
import com.agmbat.meetyou.api.Api;


public class LoginManager {


    public interface OnLoginListener {
        public void onLogin(boolean result);
    }

    public interface OnGetVerificationCodeListener {
        public void onGetVerificationCode();
    }

    public interface OnRegisterListener {
        public void onRegister(boolean result);
    }

    private Context mContext;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public LoginManager(Context activity) {
        mContext = activity.getApplicationContext();
    }

    /**
     * 登陆
     *
     * @param userName
     * @param password
     * @param l
     */
    public void login(final String userName, final String password, final OnLoginListener l) {
        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password)) {
            if (l != null) {
                l.onLogin(true);
            }
        } else {
            ToastUtil.showToastLong("请填写账号或密码！");
        }
    }

    /**
     * 获取验证码
     */
    public void getCode(final String phone, final OnGetVerificationCodeListener l) {
        AsyncTaskUtils.executeAsyncTask(new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return Api.getVerificationCode(phone);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (l != null) {
                    l.onGetVerificationCode();
                }
            }
        });
    }

    /**
     * 注册
     */
    public void register(final String name, final String pwd, final OnRegisterListener l) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (l != null) {
                    l.onRegister(true);
                }
            }
        }, 3000);
    }

}
