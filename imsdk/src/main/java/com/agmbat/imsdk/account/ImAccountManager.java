package com.agmbat.imsdk.account;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.agmbat.android.task.AsyncTask;
import com.agmbat.android.task.AsyncTaskUtils;
import com.agmbat.imsdk.asmack.XMPPManager;

/**
 * 对UI层提供账号管理
 */
public class ImAccountManager {

    /**
     * 调试开关,是否开启检测sms
     */
    public static final boolean DEBUG_CHECK_SMS = true;

    /**
     * 登陆回调
     */
    public interface OnLoginListener {
        public void onLogin(ApiResult result);
    }

    public interface OnGetVerificationCodeListener {
        public void onGetVerificationCode(ApiResult result);
    }

    public interface OnRegisterListener {
        public void onRegister(ApiResult result);
    }

    /**
     * 修改密码回调
     */
    public interface OnChangePasswordListener {
        public void onChangePassword(ApiResult result);
    }

    public interface OnResetPasswordListener {
        public void onResetPassword(ApiResult result);
    }

    private Context mContext;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public ImAccountManager(Context activity) {
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
        AsyncTaskUtils.executeAsyncTask(new AsyncTask<Void, Void, ApiResult>() {
            @Override
            protected ApiResult doInBackground(Void... voids) {
                return AccountHelper.requestLogin(userName, password);
            }

            @Override
            protected void onPostExecute(ApiResult result) {
                super.onPostExecute(result);
                if (l != null) {
                    l.onLogin(result);
                }
            }
        });
    }


    /**
     * 获取注册验证码
     */
    public void getRegisterVerificationCode(final String phone, final OnGetVerificationCodeListener l) {
        AsyncTaskUtils.executeAsyncTask(new AsyncTask<Void, Void, ApiResult>() {
            @Override
            protected ApiResult doInBackground(Void... voids) {
                return AccountHelper.requestRegisterVerificationCode(phone);
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
     * 获取重置密码验证码
     */
    public void getResetVerificationCode(final String phone, final OnGetVerificationCodeListener l) {
        AsyncTaskUtils.executeAsyncTask(new AsyncTask<Void, Void, ApiResult>() {
            @Override
            protected ApiResult doInBackground(Void... voids) {
                return AccountHelper.requestResetVerificationCode(phone);
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
    public void register(final RegisterInfo registerInfo, final OnRegisterListener l) {
        AsyncTaskUtils.executeAsyncTask(new AsyncTask<Void, Void, ApiResult>() {
            @Override
            protected ApiResult doInBackground(Void... voids) {
                return AccountHelper.requestRegister(registerInfo);
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
     * 修改密码api
     *
     * @param oldPassword
     * @param newPassword
     * @param confirmPwd
     * @param l
     */
    public void changePassword(final String oldPassword, final String newPassword, final String confirmPwd, final OnChangePasswordListener l) {
        AsyncTaskUtils.executeAsyncTask(new AsyncTask<Void, Void, ApiResult>() {
            @Override
            protected ApiResult doInBackground(Void... voids) {
                return AccountHelper.requestChangePassword(oldPassword, newPassword, confirmPwd);
            }

            @Override
            protected void onPostExecute(ApiResult result) {
                super.onPostExecute(result);
                if (l != null) {
                    l.onChangePassword(result);
                }
            }
        });
    }

    /**
     * 重置密码
     */
    public void resetPassword(final String name, final String password, final String verificationCode, final OnResetPasswordListener l) {
        AsyncTaskUtils.executeAsyncTask(new AsyncTask<Void, Void, ApiResult>() {
            @Override
            protected ApiResult doInBackground(Void... voids) {
                return AccountHelper.requestResetPassword(name, password, verificationCode);
            }

            @Override
            protected void onPostExecute(ApiResult result) {
                super.onPostExecute(result);
                if (l != null) {
                    l.onResetPassword(result);
                }
            }
        });

    }

    public String getConnectionUserName() {
        return XMPPManager.getInstance().getConnectionUserName();
    }
}
