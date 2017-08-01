package com.maxim.common.datasource;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;



public class EncryptionUtil {

    private static final String AES_NAME = "AES";

    /**
     * just for testing
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String encryptKey = "90206f7a4fc149b592a14b7629caad5e";
        String aesEncrypt = aesEncrypt("maximsesb", encryptKey);
        System.out.println(aesEncrypt);
        System.out.println(aesDecrypt("JiVTFjWbla5wI9Hs20LHag==", encryptKey));
    }
    
    public static String decrypt(String configPassword, String aesKey) throws Exception {
        return aesDecrypt(configPassword, aesKey);
    }

    public static String binary(byte[] bytes, int radix) {
        return new BigInteger(1, bytes).toString(radix);
    }

    public static String base64Encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static byte[] base64Decode(String base64Code) throws Exception {
    	
    	
        return StringUtils.isEmpty(base64Code) ? null : Base64.getDecoder().decode(base64Code);
    }

    public static byte[] md5(byte[] bytes) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(bytes);

        return md.digest();
    }

    public static byte[] md5(String msg) throws Exception {
        return StringUtils.isEmpty(msg) ? null : md5(msg.getBytes());
    }

    public static String md5Encrypt(String msg) throws Exception {
        return StringUtils.isEmpty(msg) ? null : base64Encode(md5(msg));
    }

    public static byte[] aesEncryptToBytes(String content, String encryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance(AES_NAME);

        SecureRandom random=null;
        try {
            random = SecureRandom.getInstance("SHA1PRNG","SUN");
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        random.setSeed(encryptKey.getBytes());
        kgen.init(128, random);
//        kgen.init(128, new SecureRandom(encryptKey.getBytes()));

        Cipher cipher = Cipher.getInstance(AES_NAME);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(kgen.generateKey().getEncoded(), AES_NAME));

        return cipher.doFinal(content.getBytes("utf-8"));
    }

    public static String aesEncrypt(String content, String encryptKey) throws Exception {
        return base64Encode(aesEncryptToBytes(content, encryptKey));
    }

    public static String aesDecryptByBytes(byte[] encryptBytes, String decryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance(AES_NAME);
        SecureRandom random=null;
        try {
            random = SecureRandom.getInstance("SHA1PRNG","SUN");
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        random.setSeed(decryptKey.getBytes());
        kgen.init(128, random);

        Cipher cipher = Cipher.getInstance(AES_NAME);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(kgen.generateKey().getEncoded(), AES_NAME));
        byte[] decryptBytes = cipher.doFinal(encryptBytes);

        return new String(decryptBytes);
    }

    public static String aesDecrypt(String encryptStr, String decryptKey) throws Exception {
        return StringUtils.isEmpty(encryptStr) ? null : aesDecryptByBytes(base64Decode(encryptStr), decryptKey);
    }

}
