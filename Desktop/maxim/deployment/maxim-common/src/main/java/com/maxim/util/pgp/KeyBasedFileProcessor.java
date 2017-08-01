package com.maxim.util.pgp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Iterator;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPOnePassSignatureList;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.util.io.Streams;

public class KeyBasedFileProcessor {
	
		public static File decryptFile(
	        String inputFileName,
	        String keyFileName,
	        char[] passwd,
	        String defaultFileName)
	        throws IOException, NoSuchProviderException
	    {
	        InputStream in = new BufferedInputStream(new FileInputStream(inputFileName));
	        InputStream keyIn = new BufferedInputStream(new FileInputStream(keyFileName));
	        File decrypted =  decryptFile(in, keyIn, passwd, defaultFileName);
	        keyIn.close();
	        in.close();
	        return decrypted;
	    }

	    /**
	     * decrypt the passed in message stream
	     * @return 
	     */
	    public static File decryptFile(InputStream in, InputStream keyIn, char[]passwd, String defaultFileName)
	        throws IOException, NoSuchProviderException
	    {
	        in = PGPUtil.getDecoderStream(in);
	        
	        try
	        {
	            PGPObjectFactory pgpF = new PGPObjectFactory(in);
	            PGPEncryptedDataList    enc;

	            Object                  o = pgpF.nextObject();
	            //
	            // the first object might be a PGP marker packet.
	            //
	            if (o instanceof PGPEncryptedDataList)
	            {
	                enc = (PGPEncryptedDataList)o;
	            }
	            else
	            {
	                enc = (PGPEncryptedDataList)pgpF.nextObject();
	            }
	            
	            //
	            // find the secret key
	            //
	            Iterator                    it = enc.getEncryptedDataObjects();
	            PGPPrivateKey               sKey = null;
	            PGPPublicKeyEncryptedData   pbe = null;
	            PGPSecretKeyRingCollection  pgpSec = new PGPSecretKeyRingCollection(PGPUtil.getDecoderStream(keyIn));

	            while (sKey == null && it.hasNext())
	            {
	                pbe = (PGPPublicKeyEncryptedData)it.next();
	                
	                sKey = CGPGPUtil16.findSecretKey(pgpSec, pbe.getKeyID(), passwd);
	            }
	            
	            if (sKey == null)
	            {
	                throw new IllegalArgumentException("secret key for message not found.");
	            }
	    
	            InputStream         clear = pbe.getDataStream(sKey, "BC");
	            
	            PGPObjectFactory    plainFact = new PGPObjectFactory(clear);
	            
	            Object              message = plainFact.nextObject();
	    
	            if (pbe.isIntegrityProtected())
	            {
	                if (!pbe.verify())
	                {
	                	throw new PGPException("message failed integrity check");
	                }
	                else
	                {
	                	throw new PGPException("message integrity check passed");
	                }
	            }
	            else
	            {
	                System.err.println("no message integrity check");
	            }
	            
	            if (message instanceof PGPCompressedData)
	            {
	                PGPCompressedData   cData = (PGPCompressedData)message;
	                PGPObjectFactory    pgpFact = new PGPObjectFactory(cData.getDataStream());
	                
	                message = pgpFact.nextObject();
	            }
	            
	            if (message instanceof PGPLiteralData)
	            {
	                PGPLiteralData ld = (PGPLiteralData)message;

	                File output = null;
	                String outFileName = ld.getFileName();
	                if (outFileName.length() == 0)
	                {
	                    outFileName = defaultFileName;
	    				File file = new File(defaultFileName);
	    				if (file.isDirectory()) {
	    					output = new File(file.getAbsolutePath() + File.separator + System.currentTimeMillis() + ".out");
	    				} else {
	    					output = file;
	    				}
	                }
	                else{
	    				File file = new File(defaultFileName);
	    				if (file.isDirectory()) {
	    					output = new File(file.getAbsolutePath() + File.separator + outFileName);
	    				} else {
	    					output = file;
	    				}	                	
	                }

	                InputStream unc = ld.getInputStream();
	                OutputStream fOut = new BufferedOutputStream(new FileOutputStream(output));
	                try{
	                	Streams.pipeAll(unc, fOut);
	                }finally {
	    				if (fOut != null) {

	    					fOut.close();
	    				}
	    			}
	                
	                return output;
	            }
	            else if (message instanceof PGPOnePassSignatureList)
	            {
	                throw new PGPException("encrypted message contains a signed message - not literal data.");
	            }
	            else
	            {
	                throw new PGPException("message is not a simple encrypted file - type unknown.");
	            }


	        }
	        catch (PGPException e)
	        {
	            System.err.println(e);
	            if (e.getUnderlyingException() != null)
	            {
	                e.getUnderlyingException().printStackTrace();
	            }
	        }
			return null;
	    }

		public static void encryptFile(String outputFileName, String inputFileName, InputStream encKeyInputStream, boolean armor,
	            boolean withIntegrityCheck) throws IOException, NoSuchProviderException, PGPException {
	        OutputStream out = new BufferedOutputStream(new FileOutputStream(outputFileName));
	        PGPPublicKey encKey = CGPGPUtil16.readPublicKey(encKeyInputStream);
	        encryptFile(out, inputFileName, encKey, armor, withIntegrityCheck);
	        out.close();
	    }
		
		private static void encryptFile(OutputStream outFileStream, String fileName, PGPPublicKey encKey, boolean armor,
				boolean withIntegrityCheck) throws IOException, NoSuchProviderException
	    {
            

	        if (armor)
	        {
	        	outFileStream = new ArmoredOutputStream(outFileStream);
	        }

	        try
	        {
	            byte[] bytes = CGPGPUtil16.compressFile(fileName, CompressionAlgorithmTags.ZIP);

	            PGPEncryptedDataGenerator encGen = new PGPEncryptedDataGenerator(
	                PGPEncryptedData.CAST5, withIntegrityCheck, new SecureRandom(), "BC");
	            encGen.addMethod(encKey);


	            OutputStream cOut = encGen.open(outFileStream, bytes.length);

	            cOut.write(bytes);
	            cOut.close();

	            if (armor)
	            {
	            	outFileStream.close();
	            }
	            
	        }
	        catch (PGPException e)
	        {
	            System.err.println(e);
	            if (e.getUnderlyingException() != null)
	            {
	                e.getUnderlyingException().printStackTrace();
	            }
	        }
	    }

}
