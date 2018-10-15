/**
 * Copyright (C) 2016 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 * <p>
 * jutils
 *
 * @author mayimchen
 * @since 2017-01-07
 */
package com.agmbat.security;

import com.agmbat.log.Log;
import com.agmbat.text.StringUtils;
import com.agmbat.utils.Base64;

import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * DES加密
 */
public class DESCrypt {

    private static final String ALGORITHM_DES = "DES";

    private static final String DES_TRANSFORMATION = "DES/CBC/PKCS5Padding";
    private static final String DES_KEY = "key123456";
    private static final String DES_IV = "12345678";

    public static String encrypt(String data) {
        return encrypt(DES_KEY, data);
    }

    public static String encrypt(String key, String data) {
        String result = null;
        try {
            result = encrypt(key, StringUtils.getUtf8OrDefaultBytes(data));
        } catch (Exception e) {
            Log.e("", e);
        }
        return result;
    }

    /**
     * Encrypt the text using DES and specified key.
     *
     * @param key  The key used to encrypt.
     * @param text The text to encrypt;
     * @return Ciphered bytes.
     */
    public static byte[] encrypt(byte[] key, String text) {
        if (StringUtils.isEmpty(text)) {
            return new byte[0];
        }
        try {
            Key secretKey = generateKey(key);
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(StringUtils.getUtf8OrDefaultBytes(text));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encrypt(String key, byte[] data) throws Exception {
        Key secretKey = generateKey(StringUtils.getAsciiBytes(key));
        Cipher cipher = Cipher.getInstance(DES_TRANSFORMATION);
        AlgorithmParameterSpec paramSpec = new IvParameterSpec(StringUtils.getAsciiBytes(DES_IV));
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec);
        byte[] bytes = cipher.doFinal(data);
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    public static String decrypt(String data) {
        return decrypt(DES_KEY, data);
    }

    public static String decrypt(String key, String data) {
        try {
            byte[] bytes = decrypt(key, Base64.decode(data, Base64.NO_WRAP));
            return StringUtils.newUtf8OrDefaultString(bytes);
        } catch (Exception e) {
            Log.e("", e);
        }
        return null;
    }

    public static byte[] decrypt(String key, byte[] data) throws Exception {
        Key secretKey = generateKey(StringUtils.getAsciiBytes(key));
        Cipher cipher = Cipher.getInstance(DES_TRANSFORMATION);
        AlgorithmParameterSpec paramSpec = new IvParameterSpec(StringUtils.getAsciiBytes(DES_IV));
        cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);
        return cipher.doFinal(data);
    }

    /**
     * Encrypt the text using DES and specified key.
     *
     * @param key  The key used to encrypt.
     * @param text The text to encrypt;
     * @return Ciphered bytes.
     */
    public static String decrypt(byte[] key, byte[] data) {
        if (null == data) {
            return null;
        }
        if (0 == data.length) {
            return "";
        }
        try {
            Key secretKey = generateKey(key);
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] plaintBytes = cipher.doFinal(data);
            return StringUtils.newUtf8OrDefaultString(plaintBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据指定key数据生成Key对象
     *
     * @param key
     * @return
     * @throws Exception
     */
    private static Key generateKey(byte[] key) throws Exception {
        DESKeySpec desKeySpec = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM_DES);
        Key secretKey = keyFactory.generateSecret(desKeySpec);
        return secretKey;
    }

}
