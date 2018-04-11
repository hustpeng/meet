package com.agmbat.imsdk.account;

import android.text.TextUtils;

import com.agmbat.android.AppResources;
import com.agmbat.imsdk.R;
import com.agmbat.imsdk.api.ApiResult;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.util.AppConfigUtils;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.util.XmppStringUtils;

/**
 * 内部处理login逻辑
 */
public class AccountHelper {

    /**
     * 请求注册逻辑
     *
     * @param userName
     * @param password
     * @return
     */
    public static ApiResult requestLogin(final String userName, final String password) {
        ApiResult loginResult = new ApiResult();
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
            loginResult.mResult = false;
            loginResult.mErrorMsg = "请填写账号或密码！";
            return loginResult;
        }
        try {
            XMPPManager.getInstance().signIn(userName, password);
            loginResult.mResult = true;
            loginResult.mErrorMsg = "登陆成功！";
            return loginResult;
        } catch (XMPPException e) {
            e.printStackTrace();
        }
        loginResult.mResult = false;
        loginResult.mErrorMsg = "登陆失败！";
        return loginResult;
    }

    /**
     * 获取注册验证码逻辑
     *
     * @param phone
     * @return
     */

    public static ApiResult requestRegisterVerificationCode(String phone) {
        ApiResult getCodeResult = new ApiResult();
        if (!ImAccountManager.DEBUG_CHECK_SMS) {
            getCodeResult.mResult = true;
            getCodeResult.mErrorMsg = "请求成功!";
            return getCodeResult;
        }

        // 检测帐号是否已被注册
        ApiResult<Boolean> result = AccountApi.existedUser(phone);
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

        ApiResult verificationCodeResult = AccountApi.getVerificationCode(phone);
        if (verificationCodeResult == null || !verificationCodeResult.mResult) {
            getCodeResult.mResult = false;
            getCodeResult.mErrorMsg = "网络请求失败!";
            return getCodeResult;
        }
        getCodeResult.mResult = true;
        getCodeResult.mErrorMsg = "请求成功!";
        return getCodeResult;
    }

    /**
     * 获取重置密码验证码逻辑
     *
     * @param phone
     * @return
     */
    public static ApiResult requestResetVerificationCode(String phone) {
        ApiResult getCodeResult = new ApiResult();
        if (!ImAccountManager.DEBUG_CHECK_SMS) {
            getCodeResult.mResult = true;
            getCodeResult.mErrorMsg = "请求成功!";
            return getCodeResult;
        }

        // 检测帐号是否已被注册
        ApiResult<Boolean> result = AccountApi.existedUser(phone);
        if (result == null || !result.mResult) {
            getCodeResult.mResult = false;
            getCodeResult.mErrorMsg = "网络请求失败!";
            return getCodeResult;
        }

        // result.mResult 为 true
        if (!result.mData) {
            getCodeResult.mResult = false;
            getCodeResult.mErrorMsg = "此账号未注册!";
            return getCodeResult;
        }

        ApiResult verificationCodeResult = AccountApi.getVerificationCode(phone);
        if (verificationCodeResult == null || !verificationCodeResult.mResult) {
            getCodeResult.mResult = false;
            getCodeResult.mErrorMsg = "网络请求失败!";
            return getCodeResult;
        }
        getCodeResult.mResult = true;
        getCodeResult.mErrorMsg = "请求成功!";
        return getCodeResult;
    }

    /**
     * 请求注册逻辑
     *
     * @param registerInfo
     * @return
     */
    public static ApiResult requestRegister(RegisterInfo registerInfo) {
        String userName = registerInfo.getUserName();
        String verificationCode = registerInfo.getVerificationCode();
        ApiResult registerResult = new ApiResult();
        if (ImAccountManager.DEBUG_CHECK_SMS) {
            ApiResult<Boolean> result = AccountApi.checkSms(userName, verificationCode);
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
        }
        boolean success = false;
        try {
            XMPPManager.getInstance().signUp(registerInfo, true);
            success = true;
        } catch (XMPPException e) {
            e.printStackTrace();
        }
        registerResult.mResult = success;
        if (success) {
            registerResult.mErrorMsg = "注册成功!";
        } else {
            registerResult.mErrorMsg = "注册失败!";
        }
        return registerResult;
    }


    /**
     * 修改密码
     *
     * @param oldPassword
     * @param newPassword
     * @param mConfirmPwd
     * @return
     */
    public static ApiResult requestChangePassword(String oldPassword, String newPassword, String mConfirmPwd) {
        ApiResult result = new ApiResult();
        if (TextUtils.isEmpty(oldPassword)) {
            result.mResult = false;
            result.mErrorMsg = AppResources.getString(R.string.old_password_invalid);
            return result;
        }

        String savedPwd = AppConfigUtils.getPassword(AppResources.getAppContext());
        if (!oldPassword.equals(savedPwd)) {
            result.mResult = false;
            result.mErrorMsg = AppResources.getString(R.string.old_password_incorrect);
            return result;
        }

        if (TextUtils.isEmpty(newPassword)) {
            result.mResult = false;
            result.mErrorMsg = AppResources.getString(R.string.new_password_nil);
            return result;
        }

        if (!newPassword.equals(mConfirmPwd)) {
            result.mResult = false;
            result.mErrorMsg = AppResources.getString(R.string.password_not_match);
            return result;
        }

        if (newPassword.equals(oldPassword)) {
            result.mResult = false;
            result.mErrorMsg = AppResources.getString(R.string.password_same);
            return result;
        }

//        try {

        // TODO 使用http api实现
//            XMPPManager.getInstance().changePassword(oldPassword, newPassword);

        String phone = XMPPManager.getInstance().getConnectionUserName();
        String token = XMPPManager.getInstance().getTokenManager().getTokenRetry();
        if (!TextUtils.isEmpty(token)) {
            ApiResult apiResult = AccountApi.changePassword(phone, token, oldPassword, newPassword);
            if (apiResult != null) {
                if (apiResult.mResult) {
                    result.mResult = true;
                    result.mErrorMsg = AppResources.getString(R.string.change_password_success);
                    AppConfigUtils.setPassword(AppResources.getAppContext(), newPassword);
                    return result;
                }
            }
        }
//        } catch (XMPPException e) {
//            e.printStackTrace();
//        }
        result.mResult = false;
        result.mErrorMsg = AppResources.getString(R.string.change_password_failed);
        return result;
    }

    /**
     * 处理重置密码逻辑
     *
     * @param phone
     * @param password
     * @param verificationCode
     * @return
     */
    public static ApiResult requestResetPassword(String phone, String password, String verificationCode) {
        ApiResult resetResult = new ApiResult();
        ApiResult result = AccountApi.resetPassword(phone, password, verificationCode);
        if (result == null) {
            resetResult.mResult = false;
            resetResult.mErrorMsg = "网络请求失败!";
            return resetResult;
        }
        if (!result.mResult) {
            resetResult.mResult = false;
            resetResult.mErrorMsg = "重置密码失败!";
            return resetResult;
        }
        resetResult.mResult = true;
        resetResult.mErrorMsg = "重置密码成功!";
        return resetResult;
    }


}
