/**
 * Copyright (C) 2016 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 *
 * jutils
 *
 * @author mayimchen
 * @since 2017-01-07
 */
package com.agmbat.security;

import com.agmbat.text.StringUtils;
import com.agmbat.utils.Base64;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加密
 */
public class AESCrypt {

    private static final String ALGORITHM = "AES";
    private static final String KEY = "1Hbfh667adfDEJ78";

    /**
     * 加密字符串,输出base64字符串
     * 
     * @param text
     * @return
     * @throws Exception
     */
    public static String encryptToBase64(String text) throws Exception {
        byte[] data = encrypt(text);
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    /**
     * 加密字符串,输出16进制字符串
     * 
     * @param value
     * @return
     * @throws Exception
     */
    public static String encryptToHexString(String text) throws Exception {
        byte[] data = encrypt(text);
        return StringUtils.asHexString(data);
    }

    public static byte[] encrypt(String text) throws Exception {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedByteValue = cipher.doFinal(text.getBytes("utf-8"));
        return encryptedByteValue;
    }

    /**
     * 解密base64字符串
     * 
     * @param value
     * @return
     * @throws Exception
     */
    public static String decryptBase64(String value) throws Exception {
        return decrypt(Base64.decode(value, Base64.DEFAULT));
    }

    /**
     * 解密16进制字符串
     * 
     * @param value
     * @return
     * @throws Exception
     */
    public static String decryptHexString(String value) throws Exception {
        return decrypt(StringUtils.fromHexString(value));
    }

    /**
     * 解密数据
     * 
     * @param data
     * @return
     * @throws Exception
     */
    private static String decrypt(byte[] data) throws Exception {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedData = cipher.doFinal(data);
        String text = new String(decryptedData, "utf-8");
        return text;
    }

    private static Key generateKey() throws Exception {
        Key key = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
        return key;
    }

}