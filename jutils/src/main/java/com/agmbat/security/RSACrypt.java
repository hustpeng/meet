/**
 * Copyright (C) 2016 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 *
 * jutils
 *
 * @author mayimchen
 * @since 2017-01-07
 */
package com.agmbat.security;

import com.agmbat.utils.Base64;
import com.agmbat.utils.Platform;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * RSA公钥/私钥/签名工具包<br/>
 * 字符串格式的密钥在未在特殊说明情况下都为BASE64编码格式<br/>
 * 由于非对称加密速度极其缓慢，一般文件不使用它来加密而是使用对称加密，<br/>
 * 非对称加密算法可以用来对对称加密的密钥加密，这样保证密钥的安全也就保证了数据的安全
 */
public class RSACrypt {

    /**
     * 签名算法
     */
    private static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    /**
     * 加密算法RSA
     */
    private static final String ALGORITHM_RSA = "RSA";

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * 生成密钥对(公钥和私钥)
     * 
     * @return
     * @throws NoSuchAlgorithmException
     * @throws Exception
     */
    public static KeyPair genKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(ALGORITHM_RSA);
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        return keyPair;
    }

    /**
     * 生成公钥
     * 
     * @param key
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws Exception
     */
    public static PublicKey genPublicKey(byte[] key) throws Exception {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    /**
     * 生成公钥
     * 
     * @param key
     * @return
     * @throws Exception
     */
    public static PrivateKey genPrivateKey(byte[] key) throws Exception {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    /**
     * 用私钥对信息生成数字签名
     * 
     * @param data 已加密数据
     * @param privateKey 私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static String sign(byte[] data, String privateKeyStr) throws Exception {
        byte[] buffer = SecurityUtil.decodeKeyData(privateKeyStr);
        PrivateKey privateK = genPrivateKey(buffer);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateK);
        signature.update(data);
        return new String(Base64.encode(signature.sign(), Base64.DEFAULT));
    }

    /**
     * 校验数字签名
     * 
     * @param data 已加密数据
     * @param publicKeyStr 公钥(BASE64编码)
     * @param sign 数字签名
     * @return
     *
     * @throws Exception
     */
    public static boolean verify(byte[] data, String publicKeyStr, String sign) throws Exception {
        // 加载公钥
        byte[] keyData = SecurityUtil.decodeKeyData(publicKeyStr);
        PublicKey publicKey = genPublicKey(keyData);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(Base64.decode(sign.getBytes(), Base64.DEFAULT));
    }

    /**
     * 私钥解密
     * 
     * @param data 已加密数据
     * @param privateKey 私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] data, String privateKeyStr) throws Exception {
        byte[] buffer = SecurityUtil.decodeKeyData(privateKeyStr);
        PrivateKey privateKey = genPrivateKey(buffer);
        Cipher cipher = getCipher(); // Cipher.getInstance(keyFactory.getAlgorithm());
        return encryptOrDecryptData(cipher, data, Cipher.DECRYPT_MODE, privateKey);
    }

    /**
     * 公钥解密
     * 
     * @param data 已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @return
     *
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] data, String publicKeyStr) throws Exception {
        // 加载公钥
        byte[] keyData = SecurityUtil.decodeKeyData(publicKeyStr);
        PublicKey publicKey = genPublicKey(keyData);
        Cipher cipher = getCipher(); // Cipher.getInstance(keyFactory.getAlgorithm());
        return encryptOrDecryptData(cipher, data, Cipher.DECRYPT_MODE, publicKey);
    }

    /**
     * 公钥加密
     *
     * @param data 源数据
     * @param publicKey 公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKeyStr) throws Exception {
        // 加载公钥
        byte[] keyData = SecurityUtil.decodeKeyData(publicKeyStr);
        PublicKey publicKey = genPublicKey(keyData);
        Cipher cipher = getCipher();
        return encryptOrDecryptData(cipher, data, Cipher.ENCRYPT_MODE, publicKey);
    }

    /**
     * 私钥加密
     * 
     * @param data 源数据
     * @param privateKey 私钥(BASE64编码)
     * @return
     *
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] data, String privateKeyStr) throws Exception {
        byte[] buffer = SecurityUtil.decodeKeyData(privateKeyStr);
        PrivateKey privateKey = genPrivateKey(buffer);
        Cipher cipher = getCipher();
        return encryptOrDecryptData(cipher, data, Cipher.ENCRYPT_MODE, privateKey);
    }

    /**
     * 解密或加密码数据
     * 
     * @param cipher
     * @param data
     * @param mode
     * @param key
     * @return
     * @throws Exception
     */
    private static byte[] encryptOrDecryptData(Cipher cipher, byte[] data, int mode, Key key) throws Exception {
        cipher.init(mode, key);
        int maxBlock = (mode == Cipher.ENCRYPT_MODE) ? MAX_ENCRYPT_BLOCK : MAX_DECRYPT_BLOCK;
        return doFinal(cipher, data, maxBlock);
    }

    /**
     * 对数据分段加密或解密
     * 
     * @param cipher
     * @param data
     * @param maxBlock
     * @return
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws IOException
     */
    private static byte[] doFinal(Cipher cipher, byte[] data, int maxBlock) throws IllegalBlockSizeException,
            BadPaddingException, IOException {
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密或者解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > maxBlock) {
                cache = cipher.doFinal(data, offSet, maxBlock);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * maxBlock;
        }
        byte[] result = out.toByteArray();
        out.close();
        return result;
    }

    private static boolean IS_ANDROID_PLATFORM = Platform.isAndroid();

    private static Cipher getCipher() throws NoSuchAlgorithmException, NoSuchPaddingException {
        String cipherName = "RSA";
        if (IS_ANDROID_PLATFORM) {
            cipherName = "RSA/ECB/NoPadding";
        } else {
            cipherName = "RSA/ECB/PKCS1Padding";
            // 这里必须是RSA/NONE/PKCS1Padding,而不是RSA,否则android平台加解密会跟服务器不对应，而windows平台只需要是RSA
            // mac os Cannot find any provider supporting RSA/NONE/PKCS1Padding
            // cipherName = "RSA/NONE/PKCS1Padding";
        }
        return Cipher.getInstance(cipherName);
    }

    // java
    // Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    // android
    // Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
    //
    // 参考:
    // http://stackoverflow.com/questions/6069369/rsa-encryption-difference-between-java-and-android
    // http://stackoverflow.com/questions/2956647/rsa-encrypt-with-base64-encoded-public-key-in-android

    public static byte[] decrypt(byte[] data, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public static byte[] encrypt(byte[] data, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public static byte[] encryptWithCBC(byte[] base64keydata, byte[] textdata) {
        String cipherName = "RSA/NONE/PKCS1Padding";
        try {
            byte[] keydata = Base64.decode(base64keydata, Base64.DEFAULT);
            PublicKey key = genPublicKey(keydata);

            Cipher cipher = Cipher.getInstance(cipherName);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            int blocksize = cipher.getBlockSize();

            byte[] iv = new byte[blocksize + 11];
            SecureRandom srandom = new SecureRandom();
            srandom.nextBytes(iv);
            out.write(iv);

            int blockcount = textdata.length / blocksize;
            int remain = textdata.length % blocksize;

            byte[] buf = new byte[blocksize];
            for (int i = 0; i < blockcount; i++) {
                for (int j = 0; j < blocksize; j++) {
                    buf[j] = (byte) (textdata[blocksize * i + j] ^ iv[j]);
                }
                byte[] encryptedBuf = cipher.doFinal(buf);
                iv = encryptedBuf;
                out.write(encryptedBuf);
            }

            if (remain > 0) {
                for (int j = 0; j < remain; j++) {
                    buf[j] = (byte) (textdata[blocksize * blockcount + j] ^ iv[j]);
                }
                byte[] encryptedBuf = cipher.doFinal(buf, 0, remain);
                out.write(encryptedBuf);
            }
            byte[] encryptedData = out.toByteArray();
            out.close();
            return encryptedData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
