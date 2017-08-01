package com.maxim.util.pgp;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.SignatureException;
import java.util.Date;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSignature;


public final class RSAKeyPairGenerator
{
	
    public static void generateKeys(String secretPath, String pubPath, String identity, String password, boolean armor) throws Exception
    {
        Security.addProvider(new BouncyCastleProvider());

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "BC");
        
        kpg.initialize(1024);
        
        KeyPair kp = kpg.generateKeyPair();
                  
        FileOutputStream    out1 = new FileOutputStream(secretPath);
        FileOutputStream    out2 = new FileOutputStream(pubPath);
            
        exportKeyPair(out1, out2, kp.getPublic(), kp.getPrivate(), identity, password.toCharArray(), armor);

    }
    
    private static void exportKeyPair(
        OutputStream    secretOut,
        OutputStream    publicOut,
        PublicKey       publicKey,
        PrivateKey      privateKey,
        String          identity,
        char[]          passPhrase,
        boolean         armor)
        throws IOException, InvalidKeyException, NoSuchProviderException, SignatureException, PGPException
    {    
        if (armor)
        {
            secretOut = new ArmoredOutputStream(secretOut);
        }

        PGPSecretKey    secretKey = new PGPSecretKey(PGPSignature.DEFAULT_CERTIFICATION, PGPPublicKey.RSA_GENERAL, publicKey, privateKey, new Date(), identity, PGPEncryptedData.CAST5, passPhrase, null, null, new SecureRandom(), "BC");
        
        secretKey.encode(secretOut);
        
        secretOut.close();
        
        if (armor)
        {
            publicOut = new ArmoredOutputStream(publicOut);
        }

        PGPPublicKey    key = secretKey.getPublicKey();
        
        key.encode(publicOut);
        
        publicOut.close();
    }
    

}
