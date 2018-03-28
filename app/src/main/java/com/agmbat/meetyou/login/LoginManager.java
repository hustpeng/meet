package com.agmbat.meetyou.login;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.agmbat.android.task.AsyncTask;
import com.agmbat.android.task.AsyncTaskUtils;
import com.agmbat.android.utils.ToastUtil;
import com.agmbat.meetyou.api.Api;
import com.agmbat.meetyou.api.ApiResult;


public class LoginManager {


    public interface OnLoginListener {
        public void onLogin(boolean result);
    }

    public interface OnGetVerificationCodeListener {
        public void onGetVerificationCode(ApiResult result);
    }

    public interface OnRegisterListener {
        public void onRegister(ApiResult result);
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
    public void getVerificationCode(final String phone, final OnGetVerificationCodeListener l) {
        AsyncTaskUtils.executeAsyncTask(new AsyncTask<Void, Void, ApiResult>() {
            @Override
            protected ApiResult doInBackground(Void... voids) {
                return requestVerificationCode(phone);
            }

            @Override
            protected void onPostExecute(ApiResult result) {
                super.onPostExecute(result);
                if (l != null) {
                    l.onGetVerificationCode(result);
                }
            }
        });
    }

    /**
     * 注册
     */
    public void register(final String name, final String pwd, final String code, final OnRegisterListener l) {
        AsyncTaskUtils.executeAsyncTask(new AsyncTask<Void, Void, ApiResult>() {
            @Override
            protected ApiResult doInBackground(Void... voids) {
                return requestRegister(name, pwd, code);
            }

            @Override
            protected void onPostExecute(ApiResult result) {
                super.onPostExecute(result);
                if (l != null) {
                    l.onRegister(result);
                }
            }
        });
    }

    /**
     * 请求注册逻辑
     *
     * @param userName
     * @param password
     * @param code
     * @return
     */
    private ApiResult requestRegister(String userName, String password, String code) {
        ApiResult registerResult = new ApiResult();
        ApiResult<Boolean> result = Api.checkSms(userName, code);
        if (result == null || !result.mResult) {
            registerResult.mResult = false;
            registerResult.mErrorMsg = "网络请求失败!";
            return registerResult;
        }
        if (!result.mData) {
            registerResult.mResult = false;
            registerResult.mErrorMsg = "验证码错误!";
            return registerResult;
        }
        registerResult.mResult = true;
        registerResult.mErrorMsg = "注册成功!";
        return registerResult;
    }

    /**
     * 获取验证码逻辑
     *
     * @param phone
     * @return
     */
    private static ApiResult requestVerificationCode(String phone) {
        ApiResult getCodeResult = new ApiResult();

        // 检测帐号是否已被注册
        ApiResult<Boolean> result = Api.existedUser(phone);
        if (result == null || !result.mResult) {
            getCodeResult.mResult = false;
            getCodeResult.mErrorMsg = "网络请求失败!";
            return getCodeResult;
        }

        // result.mResult 为 true
        if (result.mData) {
            getCodeResult.mResult = false;
            getCodeResult.mErrorMsg = "此账号已被注册!";
            return getCodeResult;
        }

        ApiResult verificationCodeResult = Api.getVerificationCode(phone);
        if (verificationCodeResult == null || !verificationCodeResult.mResult) {
            getCodeResult.mResult = false;
            getCodeResult.mErrorMsg = "网络请求失败!";
            return getCodeResult;
        }
        getCodeResult.mResult = true;
        getCodeResult.mErrorMsg = "请求成功!";
        return getCodeResult;
    }

}
