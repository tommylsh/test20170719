package com.maxim.util;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;



public class EncryptionUtil {

    private static final String AES_NAME = "AES";

//    /**
//     * just for testing
//     * @param args
//     * @throws Exception
//     */
//    public static void main(String[] args) throws Exception {
//        String encryptKey = "90206f7a4fc149b592a14b7629caad5e";
//        String aesEncrypt = aesEncrypt("8ErZqSgT", encryptKey);
//        System.out.println(aesEncrypt);
//        System.out.println(aesDecrypt("zutdCMmyR26oH2fG0CnZpg==", encryptKey));
//    }
    
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
    	
    	
        return isEmpty(base64Code) ? null : Base64.getDecoder().decode(base64Code);
    }

    public static byte[] md5(byte[] bytes) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(bytes);

        return md.digest();
    }

    public static byte[] md5(String msg) throws Exception {
        return isEmpty(msg) ? null : md5(msg.getBytes());
    }

    public static String md5Encrypt(String msg) throws Exception {
        return isEmpty(msg) ? null : base64Encode(md5(msg));
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
        return isEmpty(encryptStr) ? null : aesDecryptByBytes(base64Decode(encryptStr), decryptKey);
    }
    
    public static boolean isEmpty(String str)
    {
    	return str == null || str.length() ==0 ;
    }
    
    public  static void main(String[] arg) throws Exception
    {
   		boolean showHelp = false ;

    	if (arg.length == 0)
    	{
    		showHelp = true ;
    	}
    	else if (arg[0].equals("help"))
    	{
    		showHelp = true ;
    	} else 
    	{
    		if (arg[0].equals("key"))
	    	{
	    		Key key;
	    		SecureRandom rand = new SecureRandom();
	    		KeyGenerator generator = KeyGenerator.getInstance("AES");
	    		generator.init(rand);
	    		generator.init(256);
	    		key = generator.generateKey();
	    		System.out.println(base64Encode(key.getEncoded()));
	    	}
    		else
    		{
	    		String key = null ;
	    		String filename = null ;
	    		if (arg.length < 1)
	    		{
	    			showHelp = true;
	    		}
	    		else if (arg.length < 3)
	    		{
	    			filename = "server-key.properties";
	    		}
	    		else
	    		{
	    			filename = arg[3];
	    		}
	    		
	    		File file = new File(filename);
	    		if (file.exists())
	    		{
	        		FileInputStream in = new FileInputStream(file);
	    			Properties prop = new Properties();
	    			prop.load(in);
	    			in.close();
					key = prop.getProperty("aesKey");
	    		}
	    		if (key == null)
	    		{
		    		if (arg.length == 3)
		    		{
		    			key = arg[3];
		    		}
	    		}
	    		
	    		if (key == null)
	    		{
	    			showHelp = true;
	    		} 
	    		else if (arg[0].equals("encrypt"))
		    	{
	    			System.out.println(aesEncrypt(arg[1] , key));
		    	} else if (arg[0].equals("decrypt"))
		    	{
	    			System.out.println(decrypt(arg[1] , key));
		    	}
    		}
    	}
    		
    	if (showHelp)
    	{
    		System.out.println("For KeyGen  : java EncryptionUtil key");
    		System.out.println("For encrypt : java EncryptionUtil encrypt password [key | Key Filename] ");
    		System.out.println("For decrypt : java EncryptionUtil decrypt password [key | Key Filename] ");
    		System.out.println("Default Key File Name : server-key.properties ");
    	}


    }

}
