/**
 * Copyright (C) 2016 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 *
 * jutils
 *
 * @author mayimchen
 * @since 2016-10-07
 */
package com.agmbat.security;

import com.agmbat.text.IntegralToString;
import com.agmbat.text.StringUtils;
import com.agmbat.utils.Base64;
import com.agmbat.io.IoUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * 安全相关的工具类
 */
public class SecurityUtil {

    private static final String ALGORITHM_MD5 = "MD5";

    private static final String DEFAULT_CHARSET = "UTF-8";

    private static final long POLY64REV = 0x95AC9329AC4BC9B5L;
    private static final long INITIALCRC = 0xFFFFFFFFFFFFFFFFL;

    private static long[] sCrcTable = new long[256];

    static {
        // http://bioinf.cs.ucl.ac.uk/downloads/crc64/crc64.c
        for (int i = 0; i < 256; i++) {
            long part = i;
            for (int j = 0; j < 8; j++) {
                long x = ((int) part & 1) != 0 ? POLY64REV : 0;
                part = (part >> 1) ^ x;
            }
            sCrcTable[i] = part;
        }
    }

    /**
     * A function thats returns a 64-bit crc for string
     *
     * @param in input string
     * @return a 64-bit crc value
     */
    public static final long crc64Long(String in) {
        if (in == null || in.length() == 0) {
            return 0;
        }
        return crc64Long(getBytes(in));
    }

    public static final long crc64Long(byte[] buffer) {
        long crc = INITIALCRC;
        for (int k = 0, n = buffer.length; k < n; ++k) {
            crc = sCrcTable[(((int) crc) ^ buffer[k]) & 0xff] ^ (crc >> 8);
        }
        return crc;
    }

    private static byte[] getBytes(String in) {
        byte[] result = new byte[in.length() * 2];
        int output = 0;
        for (char ch : in.toCharArray()) {
            result[output++] = (byte) (ch & 0xFF);
            result[output++] = (byte) (ch >> 8);
        }
        return result;
    }

    public static byte[] sha1Hash(byte[] data) {
        return hash(data, "SHA-1");
    }

    /**
     * 计算给定数据的 MD5 签名。
     *
     * @param data 要计算签名的数据。
     * @return data 的 MD5 签名。
     */
    public static String md5String(byte[] data) {
        byte[] digest = md5Hash(data);
        return StringUtils.asHexString(digest);
    }

    /**
     * 计算给定数据的 MD5 签名。
     *
     * @param data 要计算签名的数据。
     * @return data 的 MD5 签名。
     */
    public static byte[] md5Hash(byte[] data) {
        return hash(data, ALGORITHM_MD5);
    }

    public static String md5Hash(String text) {
        if (StringUtils.isEmpty(text)) {
            return "";
        }
        byte[] bytes = md5Hash(StringUtils.getBytes(text, DEFAULT_CHARSET));
        return toHexString(bytes).toLowerCase();
    }

    /**
     * 计算给定数据的 MD5 签名。
     *
     * @param value 要计算签名的数据。
     * @return value 的 MD5 签名。
     */
    public static byte[] md5(String value) {
        byte[] data;
        try {
            data = value.getBytes("utf-8");
            return md5Hash(data);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] hash(byte[] data, String algorithm) {
        if (null == data || data.length == 0) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            digest.reset();
            digest.update(data);
            return digest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String md5File(File file) {
        if (file == null || !file.exists() || file.isDirectory()) {
            return null;
        }
        InputStream fis = null;
        try {
            fis = new FileInputStream(file);
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM_MD5);
            byte[] buf = new byte[1024];
            int n = 0;
            while ((n = fis.read(buf)) > 0) {
                digest.update(buf, 0, n);
            }
            return toHexString(digest.digest());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(fis);
        }
        return null;
    }

    /**
     * 返回一个随机的 GUID 字符串。
     *
     * @return 随机的 GUID 字符串。
     */
    public static String generateGUID() {
        return UUID.randomUUID().toString();
    }

    public static String toHexString(byte[] data) {
        if (null == data || 0 == data.length) {
            return "";
        }
        return IntegralToString.bytesToHexString(data, true);
    }

    /**
     * base64编码key
     * 
     * @param key
     * @return
     */
    public static String encodeKey(Key key) {
        byte[] keyData = key.getEncoded();
        byte[] endcodeData = Base64.encode(keyData, Base64.DEFAULT);
        return StringUtils.newUtf8OrDefaultString(endcodeData);
    }

    /**
     * base64解码key
     * 
     * @param keyString
     * @return
     */
    public static byte[] decodeKeyData(String keyString) {
        byte[] encode = StringUtils.getUtf8OrDefaultBytes(keyString);
        return Base64.decode(encode, Base64.DEFAULT);
    }

}
